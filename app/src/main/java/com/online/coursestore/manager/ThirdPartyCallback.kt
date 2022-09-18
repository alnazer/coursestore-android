package com.online.coursestore.manager

import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Data
import com.online.coursestore.model.Response
import com.online.coursestore.model.ThirdPartyLogin

interface ThirdPartyCallback {
    fun onThirdPartyLogin(res: Data<Response>, provider: Int, thirdPartyLogin: ThirdPartyLogin)
    fun onErrorOccured(error: BaseResponse)
}