package com.serjardovic.testapp2;

import java.util.List;

public class ImageData {

        private List<String> images;
        private int currentPage;
        private int nextPage;

        public ImageData(List<String> images, int nextPage, int currentPage){

                this.images = images;
                this.nextPage = nextPage;
                this.currentPage = currentPage;
        }

        public List<String> getImages() {
                return images;
        }

        public void setImages(List<String> images) {
                this.images = images;
        }

        public int getCurrentPage() {
                return currentPage;
        }

        public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
        }

        public int getNextPage() {
                return nextPage;
        }

        public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
        }
}
