//
//  PostViewTableViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

protocol PostViewDelegate: class {
    func commentButtonClick(postView: PostView)
    func likeButtonClick(postView: PostView)
    func dislikeButtonClick(postView: PostView)
}

class PostView: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var postTextView: UITextView!
    @IBOutlet weak var dislikeBn: UIButton!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var likeBn: UIButton!
    @IBOutlet weak var dateLabel: UILabel!
    
    private var _delegate: PostViewDelegate?
    private var _post: Post? = nil
    
    var delegate: PostViewDelegate {
        set { _delegate = newValue }
        get { return _delegate! }
    }

    var post: Post {
        set { _post = newValue }
        get { return _post! }
    }
    
    public func configure(post: Post, delegate: PostViewDelegate) {
        userLabel.text = post.username
        postTextView.text = post.postText
        dislikeBn.setTitle("Dislike: " + String(post.dislikes), for: UIControl.State.normal)
        likeBn.setTitle("Like: " + String(post.likes), for: UIControl.State.normal)
        dateLabel.text = self.timestampToDate(timestampSec: post.creationTimestampSec)
        self.post = post
        self.delegate = delegate
    }
    
    public func configureDisableButtons(post: Post) {
        print("No delegate give, will disable post buttons!")
        userLabel.text = post.username
        postTextView.text = post.postText
        dislikeBn.setTitle("Dislike: " + String(post.dislikes), for: UIControl.State.normal)
        likeBn.setTitle("Like: " + String(post.likes), for: UIControl.State.normal)
        dateLabel.text = self.timestampToDate(timestampSec: post.creationTimestampSec)
        self.post = post
        
        dislikeBn.isEnabled = false
        likeBn.isEnabled = false
    }
    
    @IBAction func dislikeBnAction(_ sender: UIButton) {
        delegate.dislikeButtonClick(postView: self)
    }
    @IBAction func likeBnAction(_ sender: UIButton) {
        delegate.likeButtonClick(postView: self)
    }
    @IBAction func commentBnAction(_ sender: UIButton) {
        delegate.commentButtonClick(postView: self)
    }
}
