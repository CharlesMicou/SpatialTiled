package util

import scala.xml.{Elem, Node, XML}
import scala.xml.transform.{RewriteRule, RuleTransformer}

object XMLHelper {
    /**
      * Strip out label data from an xml blob.
      *
      * This currently leaves some whitespace sitting around which is a real pain.
      *
      * @param xml input xml to strip labels from
      * @param labels set of labels to strip
      * @return the stripped xml
      */
    def stripLabels(xml: Elem, labels: Set[String]): Elem = {
        val f = new RuleTransformer(stripLabelRule(labels)).transform(xml)
        XML.loadString(f.toString())
    }

    private def stripLabelRule(labels: Set[String]): RewriteRule = new RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
            case elem: Elem =>
                if (labels.exists(x => elem.label.equals(x))) {
                    Seq.empty
                } else {
                    elem
                }

            case other => other
        }
    }
}
