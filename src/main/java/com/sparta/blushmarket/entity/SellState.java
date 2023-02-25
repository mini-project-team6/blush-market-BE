package com.sparta.blushmarket.entity;

import com.sparta.blushmarket.exception.CustomException;

public enum SellState {
    SELL(SellStates.SELL),  // 사용자 권한
    SOLDOUT(SellStates.SOLDOUT);  // 관리자 권한

    private final String sellState;

    SellState(String sellstate) {
        this.sellState = sellstate;
    }

    public String getSellState() {
        return this.sellState;
    }

    public static class SellStates {
        public static final String SELL = "ROLE_SELL";
        public static final String SOLDOUT = "ROLE_SOLDOUT";
    }

    public static SellState fromInteger(int value) {
        switch (value) {
            case 0:
                return SELL;
            case 1:
                return SOLDOUT;
            default:
                throw new CustomException(ExceptionEnum.WRONG_VALUE);
        }
    }
}
