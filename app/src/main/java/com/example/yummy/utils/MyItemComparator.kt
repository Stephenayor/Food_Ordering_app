package com.example.yummy.utils

import com.example.yummy.data.repository.model.Orders


class MyItemComparator : Comparator<Orders?> {

    override fun compare(o1: Orders?, o2: Orders?): Int {
        return o2?.dateOfOrder?.let { o1?.dateOfOrder?.compareTo(it) }!!
    }
}
