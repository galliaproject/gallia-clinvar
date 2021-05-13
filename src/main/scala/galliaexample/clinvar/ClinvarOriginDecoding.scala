package galliaexample.clinvar

import aptus.Anything_

// ===========================================================================
object ClinvarOriginDecoding {

  def apply(value: String /* eg "11" */): Seq[String] =
    value match {
      case "0"          => Seq("unknown") // must handle this one differently
      case "1073741824" => Seq("other"  ) // though technically decoding it would work since it's a power of 2 as well (wasteful though)

      case valuesSum =>
        apply(
          valuesSum,
          mapping = Map(
            1          -> "germline",
            2          -> "somatic",
            4          -> "inherited",
            8          -> "paternal",
            16         -> "maternal",
            32         -> "de-novo",
            64         -> "biparental",
            128        -> "uniparental",
            256        -> "not-tested",
            512        -> "tested-inconclusive",
            1024       -> "TODO:unknown_code"))
    }

  // ===========================================================================
  def apply(value: String, mapping: Map[Int, String]): Seq[String] =
     breakDownValue(value.toInt)
       .assert(
         _.sum == value.toInt,
         brokenDownValues =>
          (value.toInt,
           value.toInt.toBinaryString,
           brokenDownValues.sum,
           brokenDownValues.mkString(",")))
        .map(mapping.apply)

  // ===========================================================================
  def breakDownValue(value: Int): Seq[Int] =
     value
       .toBinaryString            // eg for 11: 1011
       .reverse                   // eg for 11: 1101
       .zipWithIndex              // eg for 11: [(1, 0), (1, 1), (0, 2), (1, 3)]
       .filter(_._1 == '1')       // eg for 11: [(1, 0), (1, 1),       , (1, 3)]
       .map   (_._2       )       // eg for 11: [    0 ,     1 ,             3 ]
       .map(math.pow(2, _).toInt) // eg for 11: [    1,      2 ,             8 ] -> the sum of which is indeed 11
       .thn(subValues =>
         subValues
           .sorted
           .assert(_ == subValues) /* already be sorted by design */)

}

// ===========================================================================
