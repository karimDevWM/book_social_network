import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../../services/services';
import { Router } from '@angular/router';
import { BookResponse, PageResponseBookResponse } from '../../../../services/models';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  size: number = 4;
  page: number = 0;
  constructor(
    private bookService: BookService,
    private router: Router
  ){}
  message: string = '';
  level: string = 'success';

borrowBook(book: BookResponse) {
  this.message = '';
  this.bookService.borrowBook({
    'book-id':book.id as number
  }).subscribe({
    next: () => {
      this.level = 'success';
      this.message = 'Book successfully added to your list';
    },
    error: (err) => {
      console.log(err);
      this.level = 'error';
      this.message = err.error.error;
    }
  })
}

goToNextPage() {
  this.page++;
  this.findAllBooks();
}
goToLastPage() {
  this.page = this.bookResponse.totalPages as number - 1;
  this.findAllBooks();
}
goToPage(page: number) {
  this.page = page;
  this.findAllBooks();
}

goToFirstPage() {
  this.page = 0;
  this.findAllBooks();
}
goToPreviousPage() {
  this.page--;
  this.findAllBooks();
}
  
  
  ngOnInit(): void {
    this.findAllBooks();
  }

  findAllBooks() {
    this.bookService.findBooks({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (books) => {
        this.bookResponse = books;
      }
    });
  }

  get isLastPage(): boolean {
    return this.page == this.bookResponse.totalPages as number - 1;
  }
}
