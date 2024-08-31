package com.karim.book.feedback;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.karim.book.book.Book;
import com.karim.book.book.BookRepository;
import com.karim.book.common.PageResponse;
import com.karim.book.exception.OperationNotPermittedException;
import com.karim.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(
        FeedBackRequest request, 
        Authentication connectedUser
    ) {
        Book book = bookRepository.findById(request.bookId())
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "No book found with the ID : " + request.bookId()
                )
            );
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                "You cannot give your feedback to the requested book ID : " + request.bookId() + 
                " because is archived or not shareable"
            );
        }
        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException(
                "you cannot give a feedback to your own book"
            );
        }

        Feedback feedback = feedbackMapper.toFeedBack(request);

        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(
        Integer bookId, 
        int page, 
        int size, 
        Authentication connectedUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> FeedbackResponses = feedbacks.stream()
            .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
            .toList();
        return new PageResponse<>(
            FeedbackResponses,
            feedbacks.getNumber(),
            feedbacks.getSize(),
            feedbacks.getTotalElements(),
            feedbacks.getTotalPages(),
            feedbacks.isFirst(),
            feedbacks.isLast()
        );
    }
}
