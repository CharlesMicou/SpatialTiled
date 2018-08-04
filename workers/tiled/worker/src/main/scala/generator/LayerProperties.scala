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
        var hide = defaultHide
        var depth = defaultDepth
        var dummy = defaultDummy
        xml.foreach {
            case node@(a: Node) if node.attributes.get("hide_layer").isDefined =>
                hide = XMLHelper.xmlValueToBoolean(a.attributes.get("value").get.text)

            case node@(a: Node) if node.attributes.get("render_depth").isDefined =>
                depth = a.attributes.get("value").get.text.toInt

            case node@(a: Node) if node.attributes.get("dummy_field").isDefined =>
                dummy= XMLHelper.xmlValueToBoolean(a.attributes.get("value").get.text)

            case _ =>
        }
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