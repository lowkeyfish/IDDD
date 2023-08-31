package com.yujunyang.iddd.common.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tuple6<T1, T2, T3, T4, T5, T6> {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;

    @JsonCreator
    public Tuple6(
            @JsonProperty("t1") T1 t1,
            @JsonProperty("t2") T2 t2,
            @JsonProperty("t3") T3 t3,
            @JsonProperty("t4") T4 t4,
            @JsonProperty("t5") T5 t5,
            @JsonProperty("t6") T6 t6) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public T3 getT3() {
        return t3;
    }

    public T4 getT4() {
        return t4;
    }

    public T5 getT5() {
        return t5;
    }

    public T6 getT6() {
        return t6;
    }
}
