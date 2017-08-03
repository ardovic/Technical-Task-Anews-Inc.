package com.serjardovic.testapp2;

public class SinglePostResponse {

        private String[] images;
        private int current_page;
        private int next_page;

        public SinglePostResponse(String[] images, int next_page, int current_page){

                this.images = images;
                this.next_page = next_page;
                this.current_page = current_page;

        }

        public String[] getImages() {
                return images;
        }

        public void setImages(String[] images) {
                this.images = images;
        }

        public int getCurrent_page() {
                return current_page;
        }

        public void setCurrent_page(int current_page) {
                this.current_page = current_page;
        }

        public int getNext_page() {
                return next_page;
        }

        public void setNext_page(int next_page) {
                this.next_page = next_page;
        }
}
