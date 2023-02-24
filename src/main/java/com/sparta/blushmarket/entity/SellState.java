package com.sparta.blushmarket.entity;

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
}
