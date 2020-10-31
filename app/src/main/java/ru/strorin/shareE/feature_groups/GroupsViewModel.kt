package ru.strorin.shareE.feature_groups

import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.strorin.shareE.vk.models.VkGroup
import ru.strorin.shareE.vk.models.VkGroupList
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.strorin.shareE.R
import ru.strorin.shareE.feature_groups.ui.GroupListView
import ru.strorin.shareE.vk.commands.VkLastActivityPostCommand
import ru.strorin.shareE.vk.commands.VkUnsubscribeCommand
import ru.strorin.shareE.vk.requests.VkGroupsRequest


class GroupsViewModel: ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var groupList = arrayListOf<VkGroupUi>()
    private var groupToShow = arrayListOf<VkGroupUi>()

    private val selectedList = mutableSetOf<Int>()


    fun onViewIsReady(view: GroupListView) {
        view.setLoading(true)
        if (groupList.isEmpty()) {
            loadAllGroups(view)
        } else {
            view.setGroupsList(groupToShow)
        }
    }

    fun unsubscribeButtonClicked(view: GroupListView) {
        view.setLoadingButton(true)
        VK.execute(VkUnsubscribeCommand(selectedList.toIntArray()), object: VKApiCallback<List<Int>> {
            override fun success(result: List<Int>) {

                for (s in selectedList) {
                    for (g in groupToShow) {
                        if (g.id == s) {
                            groupToShow.remove(g)
                            break
                        }
                    }
                }
                selectedList.clear()
                view.setLoadingButton(false)
                view.setGroupsList(groupToShow)
            }

            override fun fail(error: Exception) {
                view.setLoadingButton(false)
                view.showToast(R.string.str_error_posting_toast)
            }
        })
    }

    fun setSelected(groupUi: VkGroupUi, number: Int) {
        if (groupUi.selected) {
            selectedList.remove(groupUi.id)
        } else {
            selectedList.add(groupUi.id)
        }
        groupUi.selected = !groupUi.selected
    }

    fun getSelectedList() = selectedList

    fun getSelectedNumber(): Int = selectedList.size

    private fun loadAllGroups(view: GroupListView) {
        VK.execute(VkGroupsRequest(), object: VKApiCallback<VkGroupList> {
            override fun success(result: VkGroupList) {

                val disposable = transformToUiModels(result.items)
                    .subscribe {
                        formGroupToShowList(view, it)
                    }

                compositeDisposable.add(disposable)
            }

            override fun fail(error: Exception) {
                view.showToast(R.string.str_error_posting_toast)
            }
        })
    }

    private fun formGroupToShowList(view: GroupListView, list: ArrayList<VkGroupUi>) {
        groupList = list
        val lessPopularDisposable = getLessPopularGroups(groupList)
            .subscribe({ lessPopular: List<VkGroupUi> ->
                groupToShow.addAll(lessPopular)
                view.setGroupsList(groupToShow)
            }, {})
        compositeDisposable.add(lessPopularDisposable)
        val oldActivity = getOldActivityGroups(groupList)
            .subscribe({oldGroups ->
                groupToShow.addAll(oldGroups)
                view.setGroupsList(groupToShow)
            }, {})
        compositeDisposable.add(oldActivity)
    }

    private fun getLessPopularGroups(itemList: List<VkGroupUi>): Flowable<MutableList<VkGroupUi>> {
        return Flowable
            .fromArray(itemList)
            .map { list ->
                var l = list.filter {
                    !it.is_admin
                }.toMutableList()
                if (l.size > 50) {
                    l = l.subList((list.size*0.6).toInt(), l.size)
                }
                l.shuffle()
                if (l.size >= 20) {
                    l = l.subList(l.size - 20, l.size)
                }
                l
            }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getOldActivityGroups(itemList: List<VkGroupUi>): Flowable<MutableList<VkGroupUi>> {
        return Flowable
            .fromArray(itemList)
            .map { list ->
                val resultRequest = VK.executeSync(
                    VkLastActivityPostCommand(
                        itemList
                            .map { it.id })
                )
                for (i in resultRequest.indices) {
                    if (list.size > i) {
                        list[i].last_activity = resultRequest[i]
                    }
                }
                val currentTime = System.currentTimeMillis()
                val threeMonth = TimeUnit.DAYS.toMillis(90)
                list.filter { group ->
                    currentTime - group.last_activity.times(1000) >= threeMonth
                            && !group.is_admin
                }.toMutableList()
            }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun transformToUiModels(itemList: List<VkGroup>): Flowable<ArrayList<VkGroupUi>> {
        return Flowable
            .fromArray(itemList)
            .map {
                val list = ArrayList<VkGroupUi>()
                for (item in itemList) {
                    list.add(
                        VkGroupUi.from(
                            item
                        )
                    )
                }
                list
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }
}