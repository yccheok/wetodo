package com.yocto.wetodo.repository;

import androidx.room.ColumnInfo;

public class UpdateOrder {
    @ColumnInfo(name = "id")
    public final long id;
    @ColumnInfo(name = "order")
    public final int order;

    public static UpdateOrder newInstace(long id, int order) {
        return new UpdateOrder(id, order);
    }

    public UpdateOrder(long id, int order) {
        this.id = id;
        this.order = order;
    }
}
