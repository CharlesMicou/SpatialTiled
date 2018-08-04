package generator

import tiled.map.{LayerGameplayProperties, LayerRenderingProperties}
import util.XMLHelper

import scala.xml.{Elem, Node, NodeSeq}

case class LayerProperties(renderingLayerProperties: RenderingLayerProperties,
                           gameplayLayerProperties: GameplayLayerProperties) {
    def toXML: Elem = {
        val merged: Seq[Elem] = Seq(renderingLayerProperties.toXML, gameplayLayerProperties.toXML).flatten
        XMLHelper.addChildren(<properties></properties>, merged)
    }

    def toSchema: (LayerRenderingProperties, LayerGameplayProperties) = {
        (renderingLayerProperties.toSchema, gameplayLayerProperties.toSchema)
    }
}

case class RenderingLayerProperties(hideLayer: Boolean, renderDepth: Int) {
    def toXML: Seq[Elem] = {
        Seq(<property name="hide_layer" value={XMLHelper.booleanToXMLValue(hideLayer)} type="bool"/>,
                <property name="render_depth" value={renderDepth.toString} type="int"/>)
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
        Seq(<property name="dummy_field" value={XMLHelper.booleanToXMLValue(dummyField)} type="bool"/>)
    }

    def toSchema: LayerGameplayProperties = {
        val asSchema = LayerGameplayProperties.create()
        asSchema.setCollisions(dummyField)
        asSchema
    }
}

object LayerProperties {
    private val defaultHide = false
    private val defaultDepth = 0
    private val defaultDummy = true

    def fromXML(xml: NodeSeq): LayerProperties = {
        val properties = xml \\ "property"

        // This is horribly ugly, but until there's a decision on what should actually
        // go into layer properties and how it exists in schema, I'm not going to
        // spend time making it better.
        var hide = defaultHide
        var depth = defaultDepth
        var dummy = defaultDummy
        val mapped = properties.map(property =>
            (property.attributes.get("name").get.text, property.attributes.get("value").get.text))
          .toMap
        mapped.get("hide_layer").foreach(a => hide = XMLHelper.xmlValueToBoolean(a))
        mapped.get("dummy_field").foreach(a => dummy = XMLHelper.xmlValueToBoolean(a))
        mapped.get("render_depth").foreach(a => depth = a.toInt)

        LayerProperties(RenderingLayerProperties(hide, depth),
            GameplayLayerProperties(dummy))
    }

    def fromSchema(renderingProperties: LayerRenderingProperties,
                   gameplayProperties: LayerGameplayProperties): LayerProperties = {
        val gameplayLayerProperties = GameplayLayerProperties(gameplayProperties.getCollisions)
        val renderingLayerProperties = RenderingLayerProperties(renderingProperties.getHideLayer,
            renderingProperties.getRenderDepth)
        LayerProperties(renderingLayerProperties, gameplayLayerProperties)
    }

}