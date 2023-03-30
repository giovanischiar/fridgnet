//
//  IconForeground.swift
//
//
//  Created by Giovani Schiar on 30/03/23.
//

struct IconForeground: Foregroundable {
    let dimensions = Traits.shared.dimensions
    var iconSize: Double { dimensions.iconSize }
    var foregroundSize: Double { dimensions.foregroundSize }
    var strokeWidth: Double { dimensions.strokeWidth }
    var handleLength: Double { dimensions.handleLength }
    var frigdeWidth: Double { dimensions.frigdeWidth }
    var freezeDoorHeight:  Double { dimensions.freezeDoorHeight }
    var doorHeight:  Double { dimensions.doorHeight }

    var handleX: Double { 0 - frigdeWidth/2 + handleLength/2 + 3 }
        
    var foreground: Foreground {
        Foreground(size: iconSize) {
            Div {
                Magnet(x: 0, y: 2)
                Handle(x: handleX, y: -11 + freezeDoorHeight - 4)
            }
            .position(x: 0, y: 0 - foregroundSize/2 + freezeDoorHeight/2)
            .dimension(width: frigdeWidth, height: freezeDoorHeight)
            .stroke(color:  -"freezeDoorStrokeColor")
            .stroke(width: strokeWidth)
            .fill(color: -"freezeDoorColor")
            .northEastRadius(rx: 5)
            .northWestRadius(rx: 5)

            Div {
                Handle(x: handleX, y: (0 - foregroundSize/2 + doorHeight/2 - 7) + 4)
            }
            .position(x: 0, y: 0 + foregroundSize/2 - doorHeight/2)
            .dimension(width: frigdeWidth, height: doorHeight)
            .stroke(color:  -"doorStrokeColor")
            .fill(color: -"doorColor")
            .stroke(width: strokeWidth)
            .southEastRadius(rx: 5)
            .southWestRadius(rx: 5)
        }
    }
}
