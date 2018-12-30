//
//  PopularCollectionViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 12/29/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

protocol PopularCollectionViewCellDelegate: class {
    func popularButtonClicked(popularCollectionViewCell: PopularCollectionViewCell)
}

class PopularCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var popularButton: UIButton!
    public private(set) var label: String = ""
    public private(set) var viewPosition: Int = 0
    
    weak var delegate: PopularCollectionViewCellDelegate?
    
    @IBAction func popularButtonAction(_ sender: UIButton) {
        delegate?.popularButtonClicked(popularCollectionViewCell: self)
    }
    
    public func configure(label: String, viewPosition: Int) {
        self.label = label
        self.viewPosition = viewPosition
        self.popularButton.setTitle(label, for: .normal)
    }
    
}
