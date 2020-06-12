package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PagingConfig;
import androidx.paging.PagingSource;

import com.yocto.wetodo.repository.TodoRepository;

public class TodoViewModel extends ViewModel {
    private static final int PAGE_SIZE = 56;

    private LiveData<PagedList<Todoable>> todoablesPagedListLiveData;

    public TodoViewModel() {
    }

    public LiveData<PagedList<Todoable>> getTodoablesPagedListLiveData() {
        // Lazy evaluation.
        if (todoablesPagedListLiveData != null) {
            return todoablesPagedListLiveData;
        }

        final int pageSize = 60;
        final int prefetchDistance = pageSize;
        final boolean enablePlaceholders = false;
        final int initialLoadSize = pageSize * PagingConfig.DEFAULT_INITIAL_PAGE_MULTIPLIER;
        final int maxSize = PagingConfig.MAX_SIZE_UNBOUNDED;
        final int jumpThreshold = PagingSource.LoadResult.Page.COUNT_UNDEFINED;

        PagingConfig pagingConfig = new PagingConfig(
                pageSize,
                prefetchDistance,
                enablePlaceholders,
                initialLoadSize,
                maxSize,
                jumpThreshold
        );

        // TODO: https://stackoverflow.com/questions/62337505/how-to-porting-paging-3-from-kotlin-flow-to-java-unable-to-find-pagingdataflo
        
        todoablesPagedListLiveData = new LivePagedListBuilder<>(
                (DataSource.Factory<Integer, Todoable>)TodoRepository.INSTANCE.getTodosDataSourceFactory(),
                PAGE_SIZE
        ).build();

        return todoablesPagedListLiveData;
    }
}
