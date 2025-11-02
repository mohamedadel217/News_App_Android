package com.example.data.repository

import com.example.common.Mapper
import com.example.common.PagingModel
import com.example.common.RepositoryException
import com.example.common.toEpochMillisFromServer
import com.example.data.model.NewDTO
import com.example.domain.entity.NewEntity
import com.example.domain.repository.NewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class NewRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val newMapper: Mapper<NewDTO, NewEntity>,
    @Named("newsSource") private val newsSource: String
) : NewRepository {

    override suspend fun getNews(page: Int): Flow<PagingModel<List<NewEntity>>> = flow {
        var remoteErr: Throwable? = null

        try {
            val remote = remoteDataSource.getNews(page, newsSource)
            val mapped = newMapper.fromList(remote.data)
            val sorted = mapped.sortedByDescending { it.publishedAt.toEpochMillisFromServer() }
            emit(PagingModel(data = sorted, total = remote.total, currentPage = page))
            return@flow
        } catch (t: Throwable) {
            remoteErr = t
        }

        var localErr: Throwable? = null
        try {
            val local = localDataSource.getNews()
            val mapped = newMapper.fromList(local.data)
            val sorted = mapped.sortedByDescending { it.publishedAt.toEpochMillisFromServer() }
            emit(PagingModel(data = sorted, total = local.total, currentPage = page))
            return@flow
        } catch (t: Throwable) {
            localErr = t
        }

        throw RepositoryException(remoteCause = remoteErr, localCause = localErr)
    }
}
