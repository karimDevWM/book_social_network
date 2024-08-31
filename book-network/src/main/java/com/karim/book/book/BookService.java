package com.karim.book.book;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.karim.book.common.PageResponse;
import com.karim.book.exception.OperationNotPermittedException;
import com.karim.book.file.FileStorageService;
import com.karim.book.history.BookTransactionHistory;
import com.karim.book.history.BookTransactionHistoryRepository;
import com.karim.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }
    
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
            .map(bookMapper::toBookResponse)
            .orElseThrow(() -> new EntityNotFoundException("No book found with ID : " + bookId));
    }
    
    public PageResponse<BookResponse> findBooks(
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by("createdDate").descending()
        );

        Page<Book> books = bookRepository.findBooks(pageable);
        List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();
        return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooks(
        int page, 
        int size, 
        Authentication connectedUser
    ) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by("createdDate").descending()
        );
        Page<Book> books = bookRepository.findAllDisplayableBook(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();
        return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(
        int page, 
        int size, 
        Authentication connectedUser
    ) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(
            page,
            size, 
            Sort.by("CreatedDate").descending()
        );
        Page<Book> books = bookRepository.findAll(
            BookSpecification.withOwnerId(
                user.getId()
            ), 
                pageable
        );
        List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();
        return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(
        int page, 
        int size, 
        Authentication connectedUser
    ) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by("createdDate").descending()
        );
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
        .findAllBorrowedBooks(
            pageable, 
            user.getId()
        );
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(
        int page, 
        int size, 
        Authentication connectedUser
    ) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by("createdDate").descending()
        );
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
            .findAllReturnedBooks(
                pageable, 
                user.getId()
            );
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot update others books shareable status"
            );
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);

        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot update others books archived status"
            );
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return null;
    }

    public Integer borrowBook(Authentication connectedUser, Integer bookId) {
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book ID : " + bookId + 
                " cannot be borrowed because is archived or not shareable"
            );
        }

        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot boorow your own book"
            );
        }

        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository
            .isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returnApproved(false)
            .build();
        
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book ID : " + bookId + 
                " cannot be borrowed because is archived or not shareable"
            );
        }
        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot boorow or return your own book"
            );
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
            .findByBookIdAndUserId(
                bookId, 
                user.getId()
            )
                .orElseThrow(() -> new OperationNotPermittedException(
                    "you did not borrow this book with ID : " + bookId
                ));
        bookTransactionHistory.setReturned(true);

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "The requested book ID : " + bookId + 
                " cannot be borrowed because is archived or not shareable"
            );
        }
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot return a book that you do not own"
            );
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
            .findByBookIdAndOwnerId(
                bookId,
                user.getId()
            )
                .orElseThrow(() -> new OperationNotPermittedException(
                    "The book with ID : " + bookId + 
                    ", is not returned yet. You cannot approve its return"
                ));
        bookTransactionHistory.setReturnApproved(true);

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + bookId
                )
            );
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover((String) bookCover);
        bookRepository.save(book);
    }
}
