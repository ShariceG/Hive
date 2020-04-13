//
//  ErrorView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/5/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import UIKit

class ErrorView: UIView {
    
    @IBOutlet weak var okButton: UIButton!
    @IBOutlet weak var errorMessageLabel: UILabel!
    
    public static func createAndShow(parent: UIView, errorMessage: String) {
        let errorView = UIView.loadFromNibNamed(nibNamed: "ErrorView") as! ErrorView
        errorView.show(parent: parent)
    }
    
    func show(parent: UIView) {
        self.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        isUserInteractionEnabled = true
        parent.addSubview(self)
        parent.bringSubviewToFront(self)
    }
}
