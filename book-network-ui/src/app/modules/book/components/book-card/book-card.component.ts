import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BookResponse } from '../../../../services/models';

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss',
})
export class BookCardComponent {
  private _book: BookResponse = {};
  private _manage: boolean = false;
  private _bookCover: string | undefined;

  public get book(): BookResponse {
    return this._book;
  }
  @Input()
  public set book(value: BookResponse) {
    this._book = value;
  }

  public get manage(): boolean {
    return this._manage;
  }

  @Input()
  public set manage(value: boolean) {
    this._manage = value;
  }

  public get bookCover(): string | undefined {
    if (this._book.cover) {
      return 'data:image/jpg;base64, ' + this._book.cover;
    }
    return 'https://cdn.pixabay.com/photo/2014/01/21/13/27/letter-249080_1280.jpg';
  }

  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private showDetails: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  onArchive() {
    this.archive.emit(this._book);
  }
  onShare() {
    this.share.emit(this._book);
  }
  onEdit() {
    this.edit.emit(this._book);
  }
  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book);
  }
  onBorrow() {
    this.borrow.emit(this._book);
  }
  onShowDetails() {
    this.showDetails.emit(this._book);
  }
}
