//
//  PostViewTableViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class PostViewTableViewCell: UITableViewCell {
    
    @IBOutlet weak var userLabel: UILabel!
    @IBOutlet weak var postTextView: UITextView!
    @IBOutlet weak var dislikeBn: UIButton!
    @IBOutlet weak var commentBn: UIButton!
    @IBOutlet weak var likeBn: UIButton!
    
    public func configure(post: Post) {
        userLabel.text = post.username
        postTextView.text = post.postText
        dislikeBn.setTitle("Dislike: " + String(post.dislikes), for: UIControlState.normal)
        likeBn.setTitle("Like: " + String(post.likes), for: UIControlState.normal)
    }
    
    @IBAction func commentBnAction(_ sender: UIButton) {
//        let secondViewController = self.storyboard?.instantiateViewController(withIdentifier: "secondViewController") as! secondViewController
//        self.navigationController.pushViewController(secondViewController, animated: true)
    }
}
