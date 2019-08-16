package com.cameo.common.data

data class PaginationInfo(val page: Int, val itemsPerPage: Int = 20) {

    companion object {
        val SINGLE_ITEM = PaginationInfo(0, 1)
    }
}