package generator

import tiled.map.LayerRenderingProperties
import util.XMLHelper

import scala.xml.Elem

case class LayerProperties(renderingLayerProperties: RenderingLayerProperties,
                           gameplayLayerProperties: GameplayLayerProperties) {
    def toXML: Elem = {
        val merged: Seq[Elem] = Seq(renderingLayerProperties.toXML, gameplayLayerProperties.toXML).flatten
        XMLHelper.addChildren(<properties></properties>, merged)
    }
}

case class RenderingLayerProperties(hideLayer: Boolean, renderDepth: Int) {
    def toXML: Seq[Elem] = {
        Seq(<property name="hide_layer" value={LayerProperties.booleanToXMLValue(hideLayer)} type="bool"/>,
            <property name ="render_depth" value={renderDepth.toString} type="int"/>)
    }

    def toSchema: LayerRenderingProperties = {
        val asSchema = LayerRenderingProperties.create()
        asSchema.setHideLayer(hideLayer)
        asSchema.setRenderDepth(renderDepth)
        asSchema
    }
}

case class GameplayLayerProperties(dummyField: Boolean) {
    def toXML: Seq[Elem] = {
        Seq(<property name="dummy_field" value={LayerProperties.booleanToXMLValue(dummyField)} type="bool"/>)
    }
}

private object LayerProperties {
    def booleanToXMLValue(x: Boolean): String = {
        if (x) {
            "true"
        } else {
            "false"
        }
    }
}