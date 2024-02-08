package com.example.reto2grupo1.data.repository.remote

import com.example.reto2grupo1.data.AuthenticationRequest
import com.example.reto2grupo1.data.AuthenticationResponse
import com.example.reto2grupo1.data.User
import com.example.reto2grupo1.data.UserUpdate
import com.example.reto2grupo1.data.UserWithRol
import com.example.reto2grupo1.data.repository.AuthenticationRepository
import com.example.reto2grupo1.utils.Resource

class RemoteAuthenticationRepository : BaseDataSource(), AuthenticationRepository {
    override suspend fun login(authenticationRequest: AuthenticationRequest): Resource<AuthenticationResponse> = getResult {
        RetrofitClient.apiInterface.login(authenticationRequest)
    }

    override suspend fun myInfo(): Resource<User> = getResult {
        RetrofitClient.apiInterface.myInfo()
    }
    override suspend fun myInfoUserWhitRol(): Resource<UserWithRol> = getResult {
        RetrofitClient.apiInterface.myInfoUserWhitRol()
    }
    override suspend fun updateUser(user : UserWithRol): Resource<UserWithRol> = getResult {
        RetrofitClient.apiInterface.update(user)
        }

    override suspend fun getAllUsers(): Resource<List<User>> = getResult{
        RetrofitClient.apiInterface.getAllUsers()
    }

}