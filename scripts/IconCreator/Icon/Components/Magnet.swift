//
//  QuestionMark.swift
//
//
//  Created by Giovani Schiar on 08/12/22.
//

struct Magnet: Tag {
    var x: Double
    var y: Double
    
    let dimensions = Traits.shared.dimensions
    var size: Double { dimensions.iconSize }
    var strokeWidth: Double { dimensions.strokeWidth }
        
    var body: [any Tag] {
        Path()
            .d(ArchPathData(x: x, y: y))
            .stroke(width: strokeWidth)
            .scaled(factor: size * 0.004)
            .stroke(color:  -"magnetStrokeColor")
            .fill(color: -"archColor")
        Div{}
            .position(x: -11.7, y: 12.2)
            .dimension(width: 7.8, height: 5)
            .stroke(color:  -"magnetStrokeColor")
            .stroke(width: strokeWidth)
            .fill(color: -"leftPoleColor")
        Div{}
            .position(x: 11.7, y: 12.2)
            .dimension(width: 7.8, height: 5)
            .stroke(color:  -"magnetStrokeColor")
            .stroke(width: strokeWidth)
            .fill(color: -"rightPoleColor")

    }
}
