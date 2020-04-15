//
//  CommentView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/8/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol CommentViewDelegate: class {
    func performAction(commentView: CommentView, actionType: ActionType)
}

class CommentView: UITableViewCell {
    
    @IBOutlet weak var commentDislikeBn: UIButton!
    @IBOutlet weak var commentLikeBn: UIButton!
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var commentTextView: UITextView!
    private(set) var comment: Comment? = nil
    

    @IBOutlet weak var userLbl: UILabel!
    @IBOutlet weak var commentText: UITextView!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var dislikeButton: UIButton!
    @IBOutlet weak var netLikesLabel: UILabel!
    
    private var _delegate: CommentViewDelegate?
    
    var delegate: CommentViewDelegate {
        set { _delegate = newValue }
        get { return _delegate! }
    }
    
    public func configure(comment: Comment) {
        userLbl.text = comment.username
        commentText.text = comment.commentText
        
        self.comment = comment
        
        likeButton.isEnabled = true;
        dislikeButton.isEnabled = true;
        let netLikes = comment.likes - comment.dislikes
        netLikesLabel.text = String(netLikes)
        dateLabel.text = comment.timeDiffToString()
        
        // Handle like/dislike behavior.
        DispatchQueue.main.async {
            switch comment.userActionType {
            case ActionType.LIKE:
                self.likeButton.tintColor = self.likeColor
                self.dislikeButton.tintColor = self.noActionColor
                self.setNetLikesColor(netLikes: netLikes)
                break;
            case ActionType.DISLIKE:
               self.likeButton.tintColor = self.noActionColor
                self.dislikeButton.tintColor = self.dislikeColor
                self.setNetLikesColor(netLikes: netLikes)
                break;
            case ActionType.NO_ACTION:
               self.likeButton.tintColor = self.noActionColor
                self.dislikeButton.tintColor = self.noActionColor
                self.setNetLikesColor(netLikes: netLikes)
                break;
            }
        }
    }
    
    private let likeColor = UIColor(red:0.00, green:0.51, blue:0.28, alpha:1.0)
    private let dislikeColor = UIColor(red:0.89, green:0.09, blue:0.04, alpha:1.0)
    private let noActionColor = UIColor(red:0.75, green:0.74, blue:0.76, alpha:1.0)
    
    public func configure(comment: Comment, delegate: CommentViewDelegate) {
        configure(comment: comment)
        self.delegate = delegate
    }
    
    public func configureDisable(comment: Comment) {
        configure(comment: comment)
        self.isUserInteractionEnabled = false
    }
    
    public func disableLikeAndDislikeButtons() {
        dislikeButton.isEnabled = false
        likeButton.isEnabled = false
    }
    
    
    @IBAction func dislikeButtonAction(_ sender: Any) {
        disableLikeAndDislikeButtons()
        switch comment!.userActionType {
        case ActionType.NO_ACTION, ActionType.DISLIKE:
            delegate.performAction(commentView: self, actionType: ActionType.LIKE)
            break;
        case ActionType.LIKE:
            delegate.performAction(commentView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    
    @IBAction func likeButtonAction(_ sender: Any) {
       disableLikeAndDislikeButtons()
        switch comment!.userActionType {
        case ActionType.NO_ACTION, ActionType.LIKE:
            delegate.performAction(commentView: self, actionType: ActionType.DISLIKE)
            break;
        case ActionType.DISLIKE:
            delegate.performAction(commentView: self, actionType: ActionType.NO_ACTION)
            break;
        }
    }
    
    
    private func setNetLikesColor(netLikes: Int) {
        if netLikes >  0 {
            self.netLikesLabel.textColor = likeColor
        } else if netLikes < 0{
            self.netLikesLabel.textColor = dislikeColor
        } else {
            self.netLikesLabel.textColor = noActionColor
        }
    }
    
}
