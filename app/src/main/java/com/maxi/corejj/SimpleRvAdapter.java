package com.maxi.corejj;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

public interface SimpleRvAdapter {
    RecyclerView.Adapter getAdapter();

    void update(List<SimpleRvData> data);

    class SimpleRvData implements Serializable {
        private String itemOne;
        private String itemTow;
        public boolean checked;

        public SimpleRvData() {
        }

        public SimpleRvData(String one, String tow) {
            itemOne = one;
            itemTow = tow;
        }

        public String getItemOne() {
            return itemOne;
        }

        public void setItemOne(String itemOne) {
            this.itemOne = itemOne;
        }

        public String getItemTow() {
            return itemTow;
        }

        public void setItemTow(String itemTow) {
            this.itemTow = itemTow;
        }
    }
}
