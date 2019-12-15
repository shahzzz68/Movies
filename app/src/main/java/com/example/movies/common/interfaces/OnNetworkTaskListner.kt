package com.example.movies.common.interfaces

import com.example.movies.network.HttpResponceItem

interface OnNetworkTaskListner {

      fun onNetworkResponse(response: HttpResponceItem)

    /**
     * HTTP network operation is successfully completed
     *
     * @param response [ HttpResponceItem]
     */
      fun onNetworkSuccess(response:  HttpResponceItem)

    /**
     * For some reasons there is/are some error(s) in network operation
     *
     * @param response [ HttpResponceItem]
     */
      fun onNetworkError(response:  HttpResponceItem)

    /**
     * For some reasons network operation has been cancelled
     *
     * @param response [ HttpResponceItem]
     */
      fun onNetworkCanceled(response:  HttpResponceItem)

    /**
     * @return true is connected else not
     */
      fun isNetworkConnected(): Boolean
}