//
//  CustomTextView.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 10/7/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class CustomTextView: UITextView, UITextViewDelegate {
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        isScrollEnabled = false
    }
    
    func textViewDidChange(_ textView: UITextView) {
        
    }
}
