
//
package gov.nih.nlm.nls.metamap.lite;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
/**
 *
 */

public class NegExPatterns {

  List<Pattern> negaRegExp = new ArrayList<Pattern>();
  List<Pattern> pnegbRegExp = new ArrayList<Pattern>();
  List<Pattern> pnegaRegExp = new ArrayList<Pattern>();
  List<Pattern> negbRegExp = new ArrayList<Pattern>();

  public NegExPatterns()
  {
  
    negaRegExp.add(Pattern.compile("absence of"));
    negaRegExp.add(Pattern.compile("adequate to rule her out for"));
    negaRegExp.add(Pattern.compile("adequate to rule her out"));
    negaRegExp.add(Pattern.compile("adequate to rule him out for"));
    negaRegExp.add(Pattern.compile("adequate to rule him out"));
    negaRegExp.add(Pattern.compile("adequate to rule out for "));
    negaRegExp.add(Pattern.compile("adequate to rule out"));
    negaRegExp.add(Pattern.compile("adequate to rule the patient out against"));
    negaRegExp.add(Pattern.compile("adequate to rule the patient out for"));
    negaRegExp.add(Pattern.compile("adequate to rule the patient out"));
    negaRegExp.add(Pattern.compile("can rule her out against"));
    negaRegExp.add(Pattern.compile("can rule her out for"));
    negaRegExp.add(Pattern.compile("can rule her out"));
    negaRegExp.add(Pattern.compile("can rule him out against"));
    negaRegExp.add(Pattern.compile("can rule him out for"));
    negaRegExp.add(Pattern.compile("can rule him out"));
    negaRegExp.add(Pattern.compile("can rule out against"));
    negaRegExp.add(Pattern.compile("can rule out for"));
    negaRegExp.add(Pattern.compile("can rule out"));
    negaRegExp.add(Pattern.compile("can rule the patient out against"));
    negaRegExp.add(Pattern.compile("can rule the patient out for"));
    negaRegExp.add(Pattern.compile("can rule the patient out"));
    negaRegExp.add(Pattern.compile("cannot see"));
    negaRegExp.add(Pattern.compile("cannot"));
    negaRegExp.add(Pattern.compile("checked for"));
    negaRegExp.add(Pattern.compile("declined"));
    negaRegExp.add(Pattern.compile("declines"));
    negaRegExp.add(Pattern.compile("denied"));
    negaRegExp.add(Pattern.compile("denies"));
    negaRegExp.add(Pattern.compile("deny"));
    negaRegExp.add(Pattern.compile("denying"));
    negaRegExp.add(Pattern.compile("did rule her out against "));
    negaRegExp.add(Pattern.compile("did rule her out for"));
    negaRegExp.add(Pattern.compile("did rule her out"));
    negaRegExp.add(Pattern.compile("did rule him out against"));
    negaRegExp.add(Pattern.compile("did rule him out for"));
    negaRegExp.add(Pattern.compile("did rule him out"));
    negaRegExp.add(Pattern.compile("did rule out against"));
    negaRegExp.add(Pattern.compile("did rule out for"));
    negaRegExp.add(Pattern.compile("did rule out"));
    negaRegExp.add(Pattern.compile("did rule the patient out against"));
    negaRegExp.add(Pattern.compile("did rule the patient out for"));
    negaRegExp.add(Pattern.compile("did rule the patient out"));
    negaRegExp.add(Pattern.compile("evaluate for"));
    negaRegExp.add(Pattern.compile("fails to reveal"));
    negaRegExp.add(Pattern.compile("free of"));
    negaRegExp.add(Pattern.compile("negative for"));
    negaRegExp.add(Pattern.compile("never developed"));
    negaRegExp.add(Pattern.compile("never had"));
    negaRegExp.add(Pattern.compile("no abnormal"));
    negaRegExp.add(Pattern.compile("no cause of"));
    negaRegExp.add(Pattern.compile("no complaints of"));
    negaRegExp.add(Pattern.compile("no evidence to suggest"));
    negaRegExp.add(Pattern.compile("no evidence"));
    negaRegExp.add(Pattern.compile("no findings of"));
    negaRegExp.add(Pattern.compile("no findings to indicate"));
    negaRegExp.add(Pattern.compile("no mammographic evidence of"));
    negaRegExp.add(Pattern.compile("no new evidence"));
    negaRegExp.add(Pattern.compile("no new"));
    negaRegExp.add(Pattern.compile("no other evidence"));
    negaRegExp.add(Pattern.compile("no radiographic evidence of"));
    negaRegExp.add(Pattern.compile("no sign of"));
    negaRegExp.add(Pattern.compile("no significant"));
    negaRegExp.add(Pattern.compile("no signs of"));
    negaRegExp.add(Pattern.compile("no suggestion of"));
    negaRegExp.add(Pattern.compile("no suspicious"));
    negaRegExp.add(Pattern.compile("no"));
    negaRegExp.add(Pattern.compile("not appear"));
    negaRegExp.add(Pattern.compile("not appreciate"));
    negaRegExp.add(Pattern.compile("not associated with"));
    negaRegExp.add(Pattern.compile("not complain of"));
    negaRegExp.add(Pattern.compile("not demonstrate"));
    negaRegExp.add(Pattern.compile("not exhibit"));
    negaRegExp.add(Pattern.compile("not feel"));
    negaRegExp.add(Pattern.compile("not had"));
    negaRegExp.add(Pattern.compile("not have"));
    negaRegExp.add(Pattern.compile("not know of"));
    negaRegExp.add(Pattern.compile("not known to have"));
    negaRegExp.add(Pattern.compile("not reveal"));
    negaRegExp.add(Pattern.compile("not see"));
    negaRegExp.add(Pattern.compile("not to be"));
    negaRegExp.add(Pattern.compile("not"));
    negaRegExp.add(Pattern.compile("patient was not"));
    negaRegExp.add(Pattern.compile("rather than"));
    negaRegExp.add(Pattern.compile("resolved"));
    negaRegExp.add(Pattern.compile("ruled her out against"));
    negaRegExp.add(Pattern.compile("ruled her out for"));
    negaRegExp.add(Pattern.compile("ruled her out"));
    negaRegExp.add(Pattern.compile("ruled him out against"));
    negaRegExp.add(Pattern.compile("ruled him out for"));
    negaRegExp.add(Pattern.compile("ruled him out"));
    negaRegExp.add(Pattern.compile("ruled out against "));
    negaRegExp.add(Pattern.compile("ruled out for "));
    negaRegExp.add(Pattern.compile("ruled out"));
    negaRegExp.add(Pattern.compile("ruled the patient out against"));
    negaRegExp.add(Pattern.compile("ruled the patient out for"));
    negaRegExp.add(Pattern.compile("ruled the patient out"));
    negaRegExp.add(Pattern.compile("rules her out for"));
    negaRegExp.add(Pattern.compile("rules her out"));
    negaRegExp.add(Pattern.compile("rules him out for"));
    negaRegExp.add(Pattern.compile("rules him out for"));
    negaRegExp.add(Pattern.compile("rules him out"));
    negaRegExp.add(Pattern.compile("rules out for"));
    negaRegExp.add(Pattern.compile("rules out"));
    negaRegExp.add(Pattern.compile("rules out"));
    negaRegExp.add(Pattern.compile("rules the patient out for"));
    negaRegExp.add(Pattern.compile("rules the patient out"));
    negaRegExp.add(Pattern.compile("sufficient to rule her out against"));
    negaRegExp.add(Pattern.compile("sufficient to rule her out for"));
    negaRegExp.add(Pattern.compile("sufficient to rule her out"));
    negaRegExp.add(Pattern.compile("sufficient to rule him out against"));
    negaRegExp.add(Pattern.compile("sufficient to rule him out for"));
    negaRegExp.add(Pattern.compile("sufficient to rule him out"));
    negaRegExp.add(Pattern.compile("sufficient to rule out against"));
    negaRegExp.add(Pattern.compile("sufficient to rule out for"));
    negaRegExp.add(Pattern.compile("sufficient to rule out"));
    negaRegExp.add(Pattern.compile("sufficient to rule the patient out against"));
    negaRegExp.add(Pattern.compile("sufficient to rule the patient out for"));
    negaRegExp.add(Pattern.compile("sufficient to rule the patient out"));
    negaRegExp.add(Pattern.compile("test for"));
    negaRegExp.add(Pattern.compile("to exclude"));
    negaRegExp.add(Pattern.compile("unremarkable for"));
    negaRegExp.add(Pattern.compile("with no"));
    negaRegExp.add(Pattern.compile("without any evidence of"));
    negaRegExp.add(Pattern.compile("without evidence"));
    negaRegExp.add(Pattern.compile("without indication of"));
    negaRegExp.add(Pattern.compile("without sign of"));
    negaRegExp.add(Pattern.compile("without"));

    pnegaRegExp.add(Pattern.compile("ought to be ruled out for"));
    pnegaRegExp.add(Pattern.compile("rule the patient out"));
    pnegaRegExp.add(Pattern.compile("rule her out"));
    pnegaRegExp.add(Pattern.compile("might be ruled out for"));
    pnegaRegExp.add(Pattern.compile("could be ruled out for"));
    pnegaRegExp.add(Pattern.compile("rule the patient out for"));
    pnegaRegExp.add(Pattern.compile("will be ruled out for"));
    pnegaRegExp.add(Pattern.compile("rule him out for"));
    pnegaRegExp.add(Pattern.compile("what must be ruled out is"));
    pnegaRegExp.add(Pattern.compile("may be ruled out for"));
    pnegaRegExp.add(Pattern.compile("r/o"));
    pnegaRegExp.add(Pattern.compile("ro"));
    pnegaRegExp.add(Pattern.compile("can be ruled out for"));
    pnegaRegExp.add(Pattern.compile("should be ruled out for"));
    pnegaRegExp.add(Pattern.compile("rule him out"));
    pnegaRegExp.add(Pattern.compile("rule out"));
    pnegaRegExp.add(Pattern.compile("rule her out for"));
    pnegaRegExp.add(Pattern.compile("rule out for"));
    pnegaRegExp.add(Pattern.compile("be ruled out for"));
    pnegaRegExp.add(Pattern.compile("must be ruled out for"));
    pnegaRegExp.add(Pattern.compile("is to be ruled out for"));

    negaRegExp.add(Pattern.compile("are ruled out"));
    negaRegExp.add(Pattern.compile("free"));
    negaRegExp.add(Pattern.compile("has been ruled out    "));
    negaRegExp.add(Pattern.compile("have been ruled out"));
    negaRegExp.add(Pattern.compile("is ruled out"));
    negaRegExp.add(Pattern.compile("unlikely"));
    negaRegExp.add(Pattern.compile("was ruled out"));

    negaRegExp.add(Pattern.compile("be ruled out"));
    negaRegExp.add(Pattern.compile("being ruled out"));
    negaRegExp.add(Pattern.compile("can be ruled out"));
    negaRegExp.add(Pattern.compile("could be ruled out"));
    negaRegExp.add(Pattern.compile("did not rule out"));
    negaRegExp.add(Pattern.compile("is to be ruled out"));
    negaRegExp.add(Pattern.compile("may be ruled out"));
    negaRegExp.add(Pattern.compile("might be ruled out"));
    negaRegExp.add(Pattern.compile("must be ruled out"));
    negaRegExp.add(Pattern.compile("not been ruled out"));
    negaRegExp.add(Pattern.compile("not ruled out"));
    negaRegExp.add(Pattern.compile("ought to be ruled out"));
    negaRegExp.add(Pattern.compile("should be ruled out"));
    negaRegExp.add(Pattern.compile("will be ruled out"));

  }
}
