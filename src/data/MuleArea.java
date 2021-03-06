package data;

import org.rspeer.runetek.api.movement.position.Area;

public enum MuleArea {
    GE_NEW(Area.rectangular(3176, 3470, 3179, 3468)),
    COOKS_GUILD(Area.rectangular(3180, 3437, 3185, 3433)),
    GE_NE(Area.rectangular(3180, 3513, 3181, 3512)),
    GE_SE(Area.rectangular(3184, 3471, 3183, 3472)),
    GE_SW(Area.rectangular(3140, 3474, 3141, 3473)),
    GE_NW(Area.rectangular(3148, 3515, 3149, 3514))
    ;

    private Area muleArea;

    MuleArea(Area  muleArea) {
        this.muleArea = muleArea;
    }

    public Area getMuleArea(){
        return muleArea;
    }
}

