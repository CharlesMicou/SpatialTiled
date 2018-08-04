package generator

import base.BaseTest


class LayerPropertiesTest extends BaseTest {
    "Converting to XML" should "result in equivalent XML" in {
        val renderingLayerProperties = RenderingLayerProperties(hideLayer = true, 123)
        val gameplayLayerProperties = GameplayLayerProperties(dummyField = false)

        val layerProperties = LayerProperties(renderingLayerProperties, gameplayLayerProperties)
        val expected = <properties><property name="hide_layer" value="true" type="bool"/><property name="render_depth" value="123" type="int"/><property name="dummy_field" value="false" type="bool"/></properties>

        layerProperties.toXML should be (expected)
    }
}
