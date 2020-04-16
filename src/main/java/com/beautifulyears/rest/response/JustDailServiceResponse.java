package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.domain.JustDailServices;

public class JustDailServiceResponse implements IResponse {

    private List<JustDailServices> justDailServicesArray = new ArrayList<JustDailServices>();

    @Override
    public List<JustDailServices> getResponse() {
        return justDailServicesArray;
    }

    public static class JustDailServicesPage {
        private List<JustDailServices> content = new ArrayList<JustDailServices>();
        private boolean lastPage;
        private long number;
        private long size;
        private long total;

        public JustDailServicesPage() {
            super();
        }

        public JustDailServicesPage(PageImpl<JustDailServices> page) {
            this.lastPage = page.isLastPage();
            this.number = page.getNumber();
            for (JustDailServices service : page.getContent()) {
                this.content.add(service);
            }
            this.size = page.getSize();
            this.total = page.getTotal();
        }

        public List<JustDailServices> getContent() {
            return content;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public void setContent(List<JustDailServices> content) {
            this.content = content;
        }

        public boolean isLastPage() {
            return lastPage;
        }

        public void setLastPage(boolean lastPage) {
            this.lastPage = lastPage;
        }

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

    }

    public static JustDailServicesPage getPage(PageImpl<JustDailServices> page) {
        JustDailServicesPage res = new JustDailServicesPage(page);
        return res;
    }

}