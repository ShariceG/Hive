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
    public private(set) var viewPosition: Int = 0
    public private(set) var location: Location = Location()
    private(set) var fetchPostsMetadata: QueryMetadata = QueryMetadata()
    weak var delegate: PopularCollectionViewCellDelegate?
    
    @IBAction func popularButtonAction(_ sender: UIButton) {
        delegate?.popularButtonClicked(popularCollectionViewCell: self)
    }
    
    public func configure(location: Location, viewPosition: Int) {
        self.location = location
        self.viewPosition = viewPosition
        self.popularButton.setTitle(location.area, for: .normal)
    }
    
}
