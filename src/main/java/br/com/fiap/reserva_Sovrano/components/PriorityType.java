package br.com.fiap.reserva_Sovrano.components;

public enum PriorityType {
    LEGAL(0),   // idosos, PCD, gestantes → top priority
    VIP_3(1),   // 3 estrelas VIP
    VIP_2(2),
    VIP_1(3),
    VIP_0(4),   // vip básico, se tiver
    NONE(5);    // comum

    private final int order;

    PriorityType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
