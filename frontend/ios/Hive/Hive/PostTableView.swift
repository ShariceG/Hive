//
//  PostTableView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class PostTableView: UITableViewController {
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Loaded...")
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refresh(_:)), for: .valueChanged)
        
        self.refreshControl = refreshControl
    }
    
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        print("Refreshing?")
        // Do your job, when done:
        refreshControl.endRefreshing()
    }
    
}
