import { Component, OnInit } from '@angular/core';
import {
  BookResponse,
  BorrowedBookResponse,
  FeedBackRequest,
  PageResponseBorrowedBookResponse,
} from '../../../../services/models';
import { BookService, FeedbackService } from '../../../../services/services';

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrl: './borrowed-book-list.component.scss',
})
export class BorrowedBookListComponent implements OnInit {
  borrowedBooks: PageResponseBorrowedBookResponse = {};
  page: number = 0;
  size: number = 5;
  // selectedBook: BorrowedBookResponse = {};
  selectedBook: BorrowedBookResponse | undefined = undefined;
  feedbackRequest: FeedBackRequest = {
    bookId: 0,
    comment: '',
    note: 0,
  };
  withFeedback: any;

  constructor(
    private bookservice: BookService,
    private feedbackService: FeedbackService
  ) {}
  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  returnBook(withFeedback: boolean) {
    this.bookservice.returnBorrowedBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if(withFeedback) {
          this.giveFeedback();
        }
        this.selectedBook = undefined,
        this.findAllBorrowedBooks();
      }
    })
  }
  giveFeedback() {
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest
    }).subscribe({
      next: () => {
      }
    })
  }

  findAllBorrowedBooks() {
    this.bookservice
      .findAllBorrowedBooks({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (resp) => {
          this.borrowedBooks = resp;
        },
      });
  }

  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  goToNextPage() {
    this.page++;
    this.findAllBorrowedBooks();
  }

  goToLastPage() {
    this.page = (this.borrowedBooks.totalPages as number) - 1;
    this.findAllBorrowedBooks();
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBorrowedBooks();
  }

  get isLastPage() {
    return this.page === (this.borrowedBooks.totalPages as number) - 1;
  }
}
