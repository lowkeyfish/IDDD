package com.yujunyang.iddd.common.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tuple2<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    @JsonCreator
    public Tuple2(
            @JsonProperty("t1") T1 t1,
            @JsonProperty("t2") T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

}
