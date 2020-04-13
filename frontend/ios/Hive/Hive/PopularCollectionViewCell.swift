//
//  PopularCollectionViewCell.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 12/29/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class PopularCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var popularButton: UIButton!
    public private(set) var viewPosition: Int = 0
    public private(set) var location: Location = Location()
    private(set) var fetchPostsMetadata: QueryMetadata = QueryMetadata()
    
    public func configure(location: Location, viewPosition: Int) {
        self.location = location
        self.viewPosition = viewPosition
        self.popularButton.setTitle(location.area.toString(), for: .normal)
        popularButton.titleLabel?.adjustsFontSizeToFitWidth = true
        popularButton.titleLabel?.textAlignment = .center
    }
    
}
