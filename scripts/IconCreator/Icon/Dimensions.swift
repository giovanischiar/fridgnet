//
//  Dimensions.swift
//
//
//  Created by Giovani Schiar on 30/03/23.
//

class Dimensions {
    let iconSize = 108.0
    let foregroundSize = 49.0
    var strokeWidth: Double { 1.8 %% iconSize }
    var handleLength = 10.0
    var frigdeWidth: Double { foregroundSize }
    var freezeDoorHeight:  Double { 72 %% foregroundSize }
    var doorHeight:  Double { 28 %% foregroundSize }
}
