//
//  X.swift
//
//
//  Created by Giovani Schiar on 08/12/22.
//

struct Handle: Tag {
    let x: Double
    let y: Double
    
    let dimensions = Traits.shared.dimensions
    var strokeWidth: Double { dimensions.strokeWidth }
    var handleLength: Double { dimensions.handleLength }
    
    var body: [any Tag] {
        Path()
            .d(LinePathData(x1: x, y1: y, x2: x + handleLength, y2: y))
            .stroke(color: -"handleColor")
            .stroke(width: strokeWidth+1)
    }
}
