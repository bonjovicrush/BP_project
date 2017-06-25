package com.ysc.BookPreview0518_ysc;

/**
 * DB에 자장된 책에 대한 값을 수집하는 클래스
 *
 */
public class Book {
    int id = 0;                                // DB id 번호
    String rating = "";                        // 별점 
    String title = "";                         // 책표지 
    String writer = "";                        // 작가, 저자 
    String year = "";                          // 출간일 
    String isbn = "";                          // ISBN 번호 
    String bookimg = "";                       // 책표지 번호 
    String time = "";                          // 현재 시간

    /**
     * 생성자
     * @param id
     * @param rating
     * @param title
     * @param writer
     * @param year
     * @param isbn
     * @param bookimg
     */
    public Book(int id, String rating, String title, String writer, String year, String isbn, String bookimg, String time) {
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.writer = writer;
        this.year = year;
        this.isbn = isbn;
        this.bookimg = bookimg;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookimg() {
        return bookimg;
    }

    public void setBookimg(String bookimg) {
        this.bookimg = bookimg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
