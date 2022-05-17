package com.example.myapplication;

public interface ItemTouchStaus {
    boolean onItemMove(int fromPosition, int toPosition);
    boolean onItemRemove(int position);
}
