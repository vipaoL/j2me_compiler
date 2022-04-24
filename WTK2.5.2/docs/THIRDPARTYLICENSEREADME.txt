DO NOT TRANSLATE OR LOCALIZE.

%% The following software may be included in this product:  sip-for-me. Use of any of this software is governed by the terms of the license below: 

Copyrights
----------

The software was developed by employees of the National Institute of
Standards and Technology (NIST), an agency of the Federal Government.
Pursuant to title 15 Untied States Code Section 105, works of NIST
employees are not subject to copyright protection in the United States
and are considered to be in the public domain.  As a result, a formal
license is not needed to use the software.

The NIST-SIP software is provided by NIST as a service and is expressly
provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
AND DATA ACCURACY.  NIST does not warrant or make any representations
regarding the use of the software or the results thereof, including but
not limited to the correctness, accuracy, reliability or usefulness of
the software.

Permission to use this software is contingent upon your acceptance
of the terms of this agreement and upon your providing appropriate
acknowledgments of NIST's ownership of the software.

%% The following software may be included in this product:  JPEG. Use of any of this software is governed by the terms of the license below: 

Taken from code......

LEGAL ISSUES
============

In plain English:

1. We don't promise that this software works.  (But if you find any bugs,
   please let us know!)
2. You can use this software for whatever you want.  You don't have to pay us.
3. You may not pretend that you wrote this software.  If you use it in a
   program, you must acknowledge somewhere in your documentation that
   you've used the IJG code.

In legalese:

The authors make NO WARRANTY or representation, either express or implied,
with respect to this software, its quality, accuracy, merchantability, or
fitness for a particular purpose.  This software is provided "AS IS", and you,
its user, assume the entire risk as to its quality and accuracy.

This software is copyright (C) 1991-1998, Thomas G. Lane.
All Rights Reserved except as specified below.

Permission is hereby granted to use, copy, modify, and distribute this
software (or portions thereof) for any purpose, without fee, subject to these
conditions:
(1) If any part of the source code for this software is distributed, then this
README file must be included, with this copyright and no-warranty notice
unaltered; and any additions, deletions, or changes to the original files
must be clearly indicated in accompanying documentation.
(2) If only executable code is distributed, then the accompanying
documentation must state that "this software is based in part on the work of
the Independent JPEG Group".
(3) Permission for use of this software is granted only if the user accepts
full responsibility for any undesirable consequences; the authors accept
NO LIABILITY for damages of any kind.

These conditions apply to any software derived from or based on the IJG code,
not just to the unmodified library.  If you use our work, you ought to
acknowledge us.

Permission is NOT granted for the use of any IJG author's name or company name
in advertising or publicity relating to this software or products derived from
it.  This software may be referred to only as "the Independent JPEG Group's
software".

We specifically permit and encourage the use of this software as the basis of
commercial products, provided that all warranty or liability claims are
assumed by the product vendor.


ansi2knr.c is included in this distribution by permission of L. Peter Deutsch,
sole proprietor of its copyright holder, Aladdin Enterprises of Menlo Park, CA.
ansi2knr.c is NOT covered by the above copyright and conditions, but instead
by the usual distribution terms of the Free Software Foundation; principally,
that you must include source code if you redistribute it.  (See the file
ansi2knr.c for full details.)  However, since ansi2knr.c is not needed as part
of any program generated from the IJG code, this does not limit you more than
the foregoing paragraphs do.

The Unix configuration script "configure" was produced with GNU Autoconf.
It is copyright by the Free Software Foundation but is freely distributable.
The same holds for its supporting scripts (config.guess, config.sub,
ltconfig, ltmain.sh).  Another support script, install-sh, is copyright
by M.I.T. but is also freely distributable.

It appears that the arithmetic coding option of the JPEG spec is covered by
patents owned by IBM, AT&T, and Mitsubishi.  Hence arithmetic coding cannot
legally be used without obtaining one or more licenses.  For this reason,
support for arithmetic coding has been removed from the free JPEG software.
(Since arithmetic coding provides only a marginal gain over the unpatented
Huffman mode, it is unlikely that very many implementations will support it.)
So far as we are aware, there are no patent restrictions on the remaining
code.

The IJG distribution formerly included code to read and write GIF files.
To avoid entanglement with the Unisys LZW patent, GIF reading support has
been removed altogether, and the GIF writer has been simplified to produce
"uncompressed GIFs".  This technique does not use the LZW algorithm; the
resulting GIF files are larger than usual, but are readable by all standard
GIF decoders.

We are required to state that
    "The Graphics Interchange Format(c) is the Copyright property of
    CompuServe Incorporated.  GIF(sm) is a Service Mark property of
    CompuServe Incorporated."


%% The following software may be included in this product:  Ant. Use of any of this software is governed by the terms of the license below: 
License
The Apache Software License Version 2.0

The Apache Software License Version 2.0 applies to all releases of Ant starting
with ant 1.6.1

/*
 *                                 Apache License
 *                           Version 2.0, January 2004
 *                        http://www.apache.org/licenses/
 *
 *   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 *   1. Definitions.
 *
 *      "License" shall mean the terms and conditions for use, reproduction,
 *      and distribution as defined by Sections 1 through 9 of this document.
 *
 *      "Licensor" shall mean the copyright owner or entity authorized by
 *      the copyright owner that is granting the License.
 *
 *      "Legal Entity" shall mean the union of the acting entity and all
 *      other entities that control, are controlled by, or are under common
 *      control with that entity. For the purposes of this definition,
 *      "control" means (i) the power, direct or indirect, to cause the
 *      direction or management of such entity, whether by contract or
 *      otherwise, or (ii) ownership of fifty percent (50%) or more of the
 *      outstanding shares, or (iii) beneficial ownership of such entity.
 *
 *      "You" (or "Your") shall mean an individual or Legal Entity
 *      exercising permissions granted by this License.
 *
 *      "Source" form shall mean the preferred form for making modifications,
 *      including but not limited to software source code, documentation
 *      source, and configuration files.
 *
 *      "Object" form shall mean any form resulting from mechanical
 *      transformation or translation of a Source form, including but
 *      not limited to compiled object code, generated documentation,
 *      and conversions to other media types.
 *
 *      "Work" shall mean the work of authorship, whether in Source or
 *      Object form, made available under the License, as indicated by a
 *      copyright notice that is included in or attached to the work
 *      (an example is provided in the Appendix below).
 *
 *      "Derivative Works" shall mean any work, whether in Source or Object
 *      form, that is based on (or derived from) the Work and for which the
 *      editorial revisions, annotations, elaborations, or other modifications
 *      represent, as a whole, an original work of authorship. For the purposes
 *      of this License, Derivative Works shall not include works that remain
 *      separable from, or merely link (or bind by name) to the interfaces of,
 *      the Work and Derivative Works thereof.
 *
 *      "Contribution" shall mean any work of authorship, including
 *      the original version of the Work and any modifications or additions
 *      to that Work or Derivative Works thereof, that is intentionally
 *      submitted to Licensor for inclusion in the Work by the copyright owner
 *      or by an individual or Legal Entity authorized to submit on behalf of
 *      the copyright owner. For the purposes of this definition, "submitted"
 *      means any form of electronic, verbal, or written communication sent
 *      to the Licensor or its representatives, including but not limited to
 *      communication on electronic mailing lists, source code control systems,
 *      and issue tracking systems that are managed by, or on behalf of, the
 *      Licensor for the purpose of discussing and improving the Work, but
 *      excluding communication that is conspicuously marked or otherwise
 *      designated in writing by the copyright owner as "Not a Contribution."
 *
 *      "Contributor" shall mean Licensor and any individual or Legal Entity
 *      on behalf of whom a Contribution has been received by Licensor and
 *      subsequently incorporated within the Work.
 *
 *   2. Grant of Copyright License. Subject to the terms and conditions of
 *      this License, each Contributor hereby grants to You a perpetual,
 *      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
 *      copyright license to reproduce, prepare Derivative Works of,
 *      publicly display, publicly perform, sublicense, and distribute the
 *      Work and such Derivative Works in Source or Object form.
 *
 *   3. Grant of Patent License. Subject to the terms and conditions of
 *      this License, each Contributor hereby grants to You a perpetual,
 *      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
 *      (except as stated in this section) patent license to make, have made,
 *      use, offer to sell, sell, import, and otherwise transfer the Work,
 *      where such license applies only to those patent claims licensable
 *      by such Contributor that are necessarily infringed by their
 *      Contribution(s) alone or by combination of their Contribution(s)
 *      with the Work to which such Contribution(s) was submitted. If You
 *      institute patent litigation against any entity (including a
 *      cross-claim or counterclaim in a lawsuit) alleging that the Work
 *      or a Contribution incorporated within the Work constitutes direct
 *      or contributory patent infringement, then any patent licenses
 *      granted to You under this License for that Work shall terminate
 *      as of the date such litigation is filed.
 *
 *   4. Redistribution. You may reproduce and distribute copies of the
 *      Work or Derivative Works thereof in any medium, with or without
 *      modifications, and in Source or Object form, provided that You
 *      meet the following conditions:
 *
 *      (a) You must give any other recipients of the Work or
 *          Derivative Works a copy of this License; and
 *
 *      (b) You must cause any modified files to carry prominent notices
 *          stating that You changed the files; and
 *
 *      (c) You must retain, in the Source form of any Derivative Works
 *          that You distribute, all copyright, patent, trademark, and
 *          attribution notices from the Source form of the Work,
 *          excluding those notices that do not pertain to any part of
 *          the Derivative Works; and
 *
 *      (d) If the Work includes a "NOTICE" text file as part of its
 *          distribution, then any Derivative Works that You distribute must
 *          include a readable copy of the attribution notices contained
 *          within such NOTICE file, excluding those notices that do not
 *          pertain to any part of the Derivative Works, in at least one
 *          of the following places: within a NOTICE text file distributed
 *          as part of the Derivative Works; within the Source form or
 *          documentation, if provided along with the Derivative Works; or,
 *          within a display generated by the Derivative Works, if and
 *          wherever such third-party notices normally appear. The contents
 *          of the NOTICE file are for informational purposes only and
 *          do not modify the License. You may add Your own attribution
 *          notices within Derivative Works that You distribute, alongside
 *          or as an addendum to the NOTICE text from the Work, provided
 *          that such additional attribution notices cannot be construed
 *          as modifying the License.
 *
 *      You may add Your own copyright statement to Your modifications and
 *      may provide additional or different license terms and conditions
 *      for use, reproduction, or distribution of Your modifications, or
 *      for any such Derivative Works as a whole, provided Your use,
 *      reproduction, and distribution of the Work otherwise complies with
 *      the conditions stated in this License.
 *
 *   5. Submission of Contributions. Unless You explicitly state otherwise,
 *      any Contribution intentionally submitted for inclusion in the Work
 *      by You to the Licensor shall be under the terms and conditions of
 *      this License, without any additional terms or conditions.
 *      Notwithstanding the above, nothing herein shall supersede or modify
 *      the terms of any separate license agreement you may have executed
 *      with Licensor regarding such Contributions.
 *
 *   6. Trademarks. This License does not grant permission to use the trade
 *      names, trademarks, service marks, or product names of the Licensor,
 *      except as required for reasonable and customary use in describing the
 *      origin of the Work and reproducing the content of the NOTICE file.
 *
 *   7. Disclaimer of Warranty. Unless required by applicable law or
 *      agreed to in writing, Licensor provides the Work (and each
 *      Contributor provides its Contributions) on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *      implied, including, without limitation, any warranties or conditions
 *      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
 *      PARTICULAR PURPOSE. You are solely responsible for determining the
 *      appropriateness of using or redistributing the Work and assume any
 *      risks associated with Your exercise of permissions under this License.
 *
 *   8. Limitation of Liability. In no event and under no legal theory,
 *      whether in tort (including negligence), contract, or otherwise,
 *      unless required by applicable law (such as deliberate and grossly
 *      negligent acts) or agreed to in writing, shall any Contributor be
 *      liable to You for damages, including any direct, indirect, special,
 *      incidental, or consequential damages of any character arising as a
 *      result of this License or out of the use or inability to use the
 *      Work (including but not limited to damages for loss of goodwill,
 *      work stoppage, computer failure or malfunction, or any and all
 *      other commercial damages or losses), even if such Contributor
 *      has been advised of the possibility of such damages.
 *
 *   9. Accepting Warranty or Additional Liability. While redistributing
 *      the Work or Derivative Works thereof, You may choose to offer,
 *      and charge a fee for, acceptance of support, warranty, indemnity,
 *      or other liability obligations and/or rights consistent with this
 *      License. However, in accepting such obligations, You may act only
 *      on Your own behalf and on Your sole responsibility, not on behalf
 *      of any other Contributor, and only if You agree to indemnify,
 *      defend, and hold each Contributor harmless for any liability
 *      incurred by, or claims asserted against, such Contributor by reason
 *      of your accepting any such warranty or additional liability.
 *
 *   END OF TERMS AND CONDITIONS
 *
 *   APPENDIX: How to apply the Apache License to your work.
 *
 *      To apply the Apache License to your work, attach the following
 *      boilerplate notice, with the fields enclosed by brackets "[]"
 *      replaced with your own identifying information. (Don't include
 *      the brackets!)  The text should be enclosed in the appropriate
 *      comment syntax for the file format. We also recommend that a
 *      file or class name and description of purpose be included on the
 *      same "printed page" as the copyright notice for easier
 *      identification within third-party archives.
 *
 *   Copyright [yyyy] Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
    

You can download the original license file here.

The License is accompanied by a NOTICE

   =========================================================================
   ==  NOTICE file corresponding to the section 4 d of                    ==
   ==  the Apache License, Version 2.0,                                   ==
   ==  in this case for the Apache Ant distribution.                      ==
   =========================================================================

   This product includes software developed by
   The Apache Software Foundation (http://www.apache.org/).

   This product includes also software developed by :
     - the W3C consortium (http://www.w3c.org) ,
     - the SAX project (http://www.saxproject.org)

   Please read the different LICENSE files present in the root directory of
   this distribution.

   The names "Ant" and  "Apache Software Foundation"  must not be used to
   endorse  or promote  products derived  from this  software without prior
   written permission. For written permission, please contact
   apache@apache.org.

The Apache Software License, Version 1.1

The Apache Software License, Version 1.1, applies to all versions of up to ant
1.6.0 included.

/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 * 
 *    Copyright (C) 2000-2003 The Apache Software Foundation. All
 *    rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Ant" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 * 
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software  consists of voluntary contributions made  by many individuals
 * on behalf of the  Apache Software Foundation.  For more  information  on the 
 * Apache Software Foundation, please see .
 *
 */

%% The following software may be included in this product:  AntContrib. Use of any of this software is governed by the terms of the license below: 
The Apache Software License Version 2.0 applies to all releases of Ant starting
with ant 1.6.1

/*
 *                                 Apache License
 *                           Version 2.0, January 2004
 *                        http://www.apache.org/licenses/
 *
 *   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 *   1. Definitions.
 *
 *      "License" shall mean the terms and conditions for use, reproduction,
 *      and distribution as defined by Sections 1 through 9 of this document.
 *
 *      "Licensor" shall mean the copyright owner or entity authorized by
 *      the copyright owner that is granting the License.
 *
 *      "Legal Entity" shall mean the union of the acting entity and all
 *      other entities that control, are controlled by, or are under common
 *      control with that entity. For the purposes of this definition,
 *      "control" means (i) the power, direct or indirect, to cause the
 *      direction or management of such entity, whether by contract or
 *      otherwise, or (ii) ownership of fifty percent (50%) or more of the
 *      outstanding shares, or (iii) beneficial ownership of such entity.
 *
 *      "You" (or "Your") shall mean an individual or Legal Entity
 *      exercising permissions granted by this License.
 *
 *      "Source" form shall mean the preferred form for making modifications,
 *      including but not limited to software source code, documentation
 *      source, and configuration files.
 *
 *      "Object" form shall mean any form resulting from mechanical
 *      transformation or translation of a Source form, including but
 *      not limited to compiled object code, generated documentation,
 *      and conversions to other media types.
 *
 *      "Work" shall mean the work of authorship, whether in Source or
 *      Object form, made available under the License, as indicated by a
 *      copyright notice that is included in or attached to the work
 *      (an example is provided in the Appendix below).
 *
 *      "Derivative Works" shall mean any work, whether in Source or Object
 *      form, that is based on (or derived from) the Work and for which the
 *      editorial revisions, annotations, elaborations, or other modifications
 *      represent, as a whole, an original work of authorship. For the purposes
 *      of this License, Derivative Works shall not include works that remain
 *      separable from, or merely link (or bind by name) to the interfaces of,
 *      the Work and Derivative Works thereof.
 *
 *      "Contribution" shall mean any work of authorship, including
 *      the original version of the Work and any modifications or additions
 *      to that Work or Derivative Works thereof, that is intentionally
 *      submitted to Licensor for inclusion in the Work by the copyright owner
 *      or by an individual or Legal Entity authorized to submit on behalf of
 *      the copyright owner. For the purposes of this definition, "submitted"
 *      means any form of electronic, verbal, or written communication sent
 *      to the Licensor or its representatives, including but not limited to
 *      communication on electronic mailing lists, source code control systems,
 *      and issue tracking systems that are managed by, or on behalf of, the
 *      Licensor for the purpose of discussing and improving the Work, but
 *      excluding communication that is conspicuously marked or otherwise
 *      designated in writing by the copyright owner as "Not a Contribution."
 *
 *      "Contributor" shall mean Licensor and any individual or Legal Entity
 *      on behalf of whom a Contribution has been received by Licensor and
 *      subsequently incorporated within the Work.
 *
 *   2. Grant of Copyright License. Subject to the terms and conditions of
 *      this License, each Contributor hereby grants to You a perpetual,
 *      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
 *      copyright license to reproduce, prepare Derivative Works of,
 *      publicly display, publicly perform, sublicense, and distribute the
 *      Work and such Derivative Works in Source or Object form.
 *
 *   3. Grant of Patent License. Subject to the terms and conditions of
 *      this License, each Contributor hereby grants to You a perpetual,
 *      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
 *      (except as stated in this section) patent license to make, have made,
 *      use, offer to sell, sell, import, and otherwise transfer the Work,
 *      where such license applies only to those patent claims licensable
 *      by such Contributor that are necessarily infringed by their
 *      Contribution(s) alone or by combination of their Contribution(s)
 *      with the Work to which such Contribution(s) was submitted. If You
 *      institute patent litigation against any entity (including a
 *      cross-claim or counterclaim in a lawsuit) alleging that the Work
 *      or a Contribution incorporated within the Work constitutes direct
 *      or contributory patent infringement, then any patent licenses
 *      granted to You under this License for that Work shall terminate
 *      as of the date such litigation is filed.
 *
 *   4. Redistribution. You may reproduce and distribute copies of the
 *      Work or Derivative Works thereof in any medium, with or without
 *      modifications, and in Source or Object form, provided that You
 *      meet the following conditions:
 *
 *      (a) You must give any other recipients of the Work or
 *          Derivative Works a copy of this License; and
 *
 *      (b) You must cause any modified files to carry prominent notices
 *          stating that You changed the files; and
 *
 *      (c) You must retain, in the Source form of any Derivative Works
 *          that You distribute, all copyright, patent, trademark, and
 *          attribution notices from the Source form of the Work,
 *          excluding those notices that do not pertain to any part of
 *          the Derivative Works; and
 *
 *      (d) If the Work includes a "NOTICE" text file as part of its
 *          distribution, then any Derivative Works that You distribute must
 *          include a readable copy of the attribution notices contained
 *          within such NOTICE file, excluding those notices that do not
 *          pertain to any part of the Derivative Works, in at least one
 *          of the following places: within a NOTICE text file distributed
 *          as part of the Derivative Works; within the Source form or
 *          documentation, if provided along with the Derivative Works; or,
 *          within a display generated by the Derivative Works, if and
 *          wherever such third-party notices normally appear. The contents
 *          of the NOTICE file are for informational purposes only and
 *          do not modify the License. You may add Your own attribution
 *          notices within Derivative Works that You distribute, alongside
 *          or as an addendum to the NOTICE text from the Work, provided
 *          that such additional attribution notices cannot be construed
 *          as modifying the License.
 *
 *      You may add Your own copyright statement to Your modifications and
 *      may provide additional or different license terms and conditions
 *      for use, reproduction, or distribution of Your modifications, or
 *      for any such Derivative Works as a whole, provided Your use,
 *      reproduction, and distribution of the Work otherwise complies with
 *      the conditions stated in this License.
 *
 *   5. Submission of Contributions. Unless You explicitly state otherwise,
 *      any Contribution intentionally submitted for inclusion in the Work
 *      by You to the Licensor shall be under the terms and conditions of
 *      this License, without any additional terms or conditions.
 *      Notwithstanding the above, nothing herein shall supersede or modify
 *      the terms of any separate license agreement you may have executed
 *      with Licensor regarding such Contributions.
 *
 *   6. Trademarks. This License does not grant permission to use the trade
 *      names, trademarks, service marks, or product names of the Licensor,
 *      except as required for reasonable and customary use in describing the
 *      origin of the Work and reproducing the content of the NOTICE file.
 *
 *   7. Disclaimer of Warranty. Unless required by applicable law or
 *      agreed to in writing, Licensor provides the Work (and each
 *      Contributor provides its Contributions) on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *      implied, including, without limitation, any warranties or conditions
 *      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
 *      PARTICULAR PURPOSE. You are solely responsible for determining the
 *      appropriateness of using or redistributing the Work and assume any
 *      risks associated with Your exercise of permissions under this License.
 *
 *   8. Limitation of Liability. In no event and under no legal theory,
 *      whether in tort (including negligence), contract, or otherwise,
 *      unless required by applicable law (such as deliberate and grossly
 *      negligent acts) or agreed to in writing, shall any Contributor be
 *      liable to You for damages, including any direct, indirect, special,
 *      incidental, or consequential damages of any character arising as a
 *      result of this License or out of the use or inability to use the
 *      Work (including but not limited to damages for loss of goodwill,
 *      work stoppage, computer failure or malfunction, or any and all
 *      other commercial damages or losses), even if such Contributor
 *      has been advised of the possibility of such damages.
 *
 *   9. Accepting Warranty or Additional Liability. While redistributing
 *      the Work or Derivative Works thereof, You may choose to offer,
 *      and charge a fee for, acceptance of support, warranty, indemnity,
 *      or other liability obligations and/or rights consistent with this
 *      License. However, in accepting such obligations, You may act only
 *      on Your own behalf and on Your sole responsibility, not on behalf
 *      of any other Contributor, and only if You agree to indemnify,
 *      defend, and hold each Contributor harmless for any liability
 *      incurred by, or claims asserted against, such Contributor by reason
 *      of your accepting any such warranty or additional liability.
 *
 *   END OF TERMS AND CONDITIONS
 *
 *   APPENDIX: How to apply the Apache License to your work.
 *
 *      To apply the Apache License to your work, attach the following
 *      boilerplate notice, with the fields enclosed by brackets "[]"
 *      replaced with your own identifying information. (Don't include
 *      the brackets!)  The text should be enclosed in the appropriate
 *      comment syntax for the file format. We also recommend that a
 *      file or class name and description of purpose be included on the
 *      same "printed page" as the copyright notice for easier
 *      identification within third-party archives.
 *
 *   Copyright [yyyy] Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */


%% The following software may be included in this product:  OpenGL ES Header Files. Use of any of this software is governed by the terms of the license below: 
SGI FREE SOFTWARE LICENSE B (Version 1.1 [02/22/2000]) 
1.Definitions.
1.1."Additional Notice Provisions" means such additional provisions as appear in the Notice in Original Code under the heading "Additional Notice Provisions."
1.2."Covered Code" means the Original Code or Modifications, or any combination thereof.
1.3."Hardware" means any physical device that accepts input, processes input, stores the results of processing, and/or provides output.
1.4."Larger Work" means a work that combines Covered Code or portions thereof with code not governed by the terms of this License.
1.5."Licensable" means having the right to grant, to the maximum extent possible, whether at the time of the initial grant or subsequently acquired, any and all of the rights conveyed herein.
1.6."License" means this document.
1.7."Licensed Patents" means patent claims Licensable by SGI that are infringed by the use or sale of Original Code or any Modifications provided by SGI, or any combination thereof.
1.8."Modifications" means any addition to or deletion from the substance or structure of the Original Code or any previous Modifications. When Covered Code is released as a series of files, a Modification is: 
A.Any addition to the contents of a file containing Original Code and/or addition to or deletion from the contents of a file containing previous Modifications.
B.Any new file that contains any part of the Original Code or previous Modifications.
1.9."Notice" means any notice in Original Code or Covered Code, as required by and in compliance with this License.
1.10."Original Code" means source code of computer software code that is described in the source code Notice required by Exhibit A as Original Code, and updates and error corrections specifically thereto.
1.11."Recipient" means an individual or a legal entity exercising rights under, and complying with all of the terms of, this License or a future version of this License issued under Section 8. For legal entities, "Recipient" includes any entity that controls, is controlled by, or is under common control with Recipient. For purposes of this definition, "control" of an entity means (a) the power, direct or indirect, to direct or manage such entity, or (b) ownership of fifty percent (50%) or more of the outstanding shares or beneficial ownership of such entity.
1.12."Recipient Patents" means patent claims Licensable by a Recipient that are infringed by the use or sale of Original Code or any Modifications provided by SGI, or any combination thereof. 
1.13."SGI" means Silicon Graphics, Inc.
1.14."SGI Patents" means patent claims Licensable by SGI other than the Licensed Patents.
2.License Grant and Restrictions.
2.1.SGI License Grant. Subject to the terms of this License and any third party intellectual property claims, for the duration of intellectual property protections inherent in the Original Code, SGI hereby grants Recipient a worldwide, royalty-free, non-exclusive license, to do the following: (i) under copyrights Licensable by SGI, to reproduce, distribute, create derivative works from, and, to the extent applicable, display and perform the Original Code and/or any Modifications provided by SGI alone and/or as part of a Larger Work; and (ii) under any Licensable Patents, to make, have made, use, sell, offer for sale, import and/or otherwise transfer the Original Code and/or any Modifications provided by SGI. Recipient accepts the terms and conditions of this License by undertaking any of the aforementioned actions. The patent license shall apply to the Covered Code if, at the time any related Modification is added, such addition of the Modification causes such combination to be covered by the Licensed Patents. The patent license in Section 2.1(ii) shall not apply to any other combinations that include the Modification. No patent license is provided under SGI Patents for infringements of SGI Patents by Modifications not provided by SGI or combinations of Original Code and Modifications not provided by SGI. 
2.2.Recipient License Grant. Subject to the terms of this License and any third party intellectual property claims, Recipient hereby grants SGI and any other Recipients a worldwide, royalty-free, non-exclusive license, under any Recipient Patents, to make, have made, use, sell, offer for sale, import and/or otherwise transfer the Original Code and/or any Modifications provided by SGI.
2.3.No License For Hardware Implementations. The licenses granted in Section 2.1 and 2.2 are not applicable to implementation in Hardware of the algorithms embodied in the Original Code or any Modifications provided by SGI .
3.Redistributions. 
3.1.Retention of Notice/Copy of License. The Notice set forth in Exhibit A, below, must be conspicuously retained or included in any and all redistributions of Covered Code. For distributions of the Covered Code in source code form, the Notice must appear in every file that can include a text comments field; in executable form, the Notice and a copy of this License must appear in related documentation or collateral where the Recipient's rights relating to Covered Code are described. Any Additional Notice Provisions which actually appears in the Original Code must also be retained or included in any and all redistributions of Covered Code.
3.2.Alternative License. Provided that Recipient is in compliance with the terms of this License, Recipient may, so long as without derogation of any of SGI's rights in and to the Original Code, distribute the source code and/or executable version(s) of Covered Code under (1) this License; (2) a license identical to this License but for only such changes as are necessary in order to clarify Recipient's role as licensor of Modifications; and/or (3) a license of Recipient's choosing, containing terms different from this License, provided that the license terms include this Section 3 and Sections 4, 6, 7, 10, 12, and 13, which terms may not be modified or superseded by any other terms of such license. If Recipient elects to use any license other than this License, Recipient must make it absolutely clear that any of its terms which differ from this License are offered by Recipient alone, and not by SGI. It is emphasized that this License is a limited license, and, regardless of the license form employed by Recipient in accordance with this Section 3.2, Recipient may relicense only such rights, in Original Code and Modifications by SGI, as it has actually been granted by SGI in this License.
3.3.Indemnity. Recipient hereby agrees to indemnify SGI for any liability incurred by SGI as a result of any such alternative license terms Recipient offers.
4.Termination. This License and the rights granted hereunder will terminate automatically if Recipient breaches any term herein and fails to cure such breach within 30 days thereof. Any sublicense to the Covered Code that is properly granted shall survive any termination of this License, absent termination by the terms of such sublicense. Provisions that, by their nature, must remain in effect beyond the termination of this License, shall survive.
5.No Trademark Or Other Rights. This License does not grant any rights to: (i) any software apart from the Covered Code, nor shall any other rights or licenses not expressly granted hereunder arise by implication, estoppel or otherwise with respect to the Covered Code; (ii) any trade name, trademark or service mark whatsoever, including without limitation any related right for purposes of endorsement or promotion of products derived from the Covered Code, without prior written permission of SGI; or (iii) any title to or ownership of the Original Code, which shall at all times remains with SGI. All rights in the Original Code not expressly granted under this License are reserved. 
6.Compliance with Laws; Non-Infringement. There are various worldwide laws, regulations, and executive orders applicable to dispositions of Covered Code, including without limitation export, re-export, and import control laws, regulations, and executive orders, of the U.S. government and other countries, and Recipient is reminded it is obliged to obey such laws, regulations, and executive orders. Recipient may not distribute Covered Code that (i) in any way infringes (directly or contributorily) any intellectual property rights of any kind of any other person or entity or (ii) breaches any representation or warranty, express, implied or statutory, to which, under any applicable law, it might be deemed to have been subject.
7.Claims of Infringement. If Recipient learns of any third party claim that any disposition of Covered Code and/or functionality wholly or partially infringes the third party's intellectual property rights, Recipient will promptly notify SGI of such claim.
8.Versions of the License. SGI may publish revised and/or new versions of the License from time to time, each with a distinguishing version number. Once Covered Code has been published under a particular version of the License, Recipient may, for the duration of the license, continue to use it under the terms of that version, or choose to use such Covered Code under the terms of any subsequent version published by SGI. Subject to the provisions of Sections 3 and 4 of this License, only SGI may modify the terms applicable to Covered Code created under this License.
9.DISCLAIMER OF WARRANTY. COVERED CODE IS PROVIDED "AS IS." ALL EXPRESS AND IMPLIED WARRANTIES AND CONDITIONS ARE DISCLAIMED, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES AND CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. SGI ASSUMES NO RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, SGI ASSUMES NO COST OR LIABILITY FOR SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY IS AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED CODE IS AUTHORIZED HEREUNDER EXCEPT SUBJECT TO THIS DISCLAIMER.
10.LIMITATION OF LIABILITY. UNDER NO CIRCUMSTANCES NOR LEGAL THEORY, WHETHER TORT (INCLUDING, WITHOUT LIMITATION, NEGLIGENCE OR STRICT LIABILITY), CONTRACT, OR OTHERWISE, SHALL SGI OR ANY SGI LICENSOR BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES OF ANY CHARACTER INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS OF GOODWILL, WORK STOPPAGE, LOSS OF DATA, COMPUTER FAILURE OR MALFUNCTION, OR ANY AND ALL OTHER COMMERCIAL DAMAGES OR LOSSES, EVEN IF SUCH PARTY SHALL HAVE BEEN INFORMED OF THE POSSIBILITY OF SUCH DAMAGES. THIS LIMITATION OF LIABILITY SHALL NOT APPLY TO LIABILITY FOR DEATH OR PERSONAL INJURY RESULTING FROM SGI's NEGLIGENCE TO THE EXTENT APPLICABLE LAW PROHIBITS SUCH LIMITATION. SOME JURISDICTIONS DO NOT ALLOW THE EXCLUSION OR LIMITATION OF INCIDENTAL OR CONSEQUENTIAL DAMAGES, SO THAT EXCLUSION AND LIMITATION MAY NOT APPLY TO RECIPIENT.
11.Indemnity. Recipient shall be solely responsible for damages arising, directly or indirectly, out of its utilization of rights under this License. Recipient will defend, indemnify and hold harmless Silicon Graphics, Inc. from and against any loss, liability, damages, costs or expenses (including the payment of reasonable attorneys fees) arising out of Recipient's use, modification, reproduction and distribution of the Covered Code or out of any representation or warranty made by Recipient.
12.U.S. Government End Users. The Covered Code is a "commercial item" consisting of "commercial computer software" as such terms are defined in title 48 of the Code of Federal Regulations and all U.S. Government End Users acquire only the rights set forth in this License and are subject to the terms of this License.
13.Miscellaneous. This License represents the complete agreement concerning the its subject matter. If any provision of this License is held to be unenforceable, such provision shall be reformed so as to achieve as nearly as possible the same legal and economic effect as the original provision and the remainder of this License will remain in effect. This License shall be governed by and construed in accordance with the laws of the United States and the State of California as applied to agreements entered into and to be performed entirely within California between California residents. Any litigation relating to this License shall be subject to the exclusive jurisdiction of the Federal Courts of the Northern District of California (or, absent subject matter jurisdiction in such courts, the courts of the State of California), with venue lying exclusively in Santa Clara County, California, with the losing party responsible for costs, including without limitation, court costs and reasonable attorneys fees and expenses. The application of the United Nations Convention on Contracts for the International Sale of Goods is expressly excluded. Any law or regulation that provides that the language of a contract shall be construed against the drafter shall not apply to this License.
Exhibit A
License Applicability. Except to the extent portions of this file are made subject to an alternative license as permitted in the SGI Free Software License B, Version 1.1 (the "License"), the contents of this file are subject only to the provisions of the License. You may not use this file except in compliance with the License. You may obtain a copy of the License at Silicon Graphics, Inc., attn: Legal Services, 1600 Amphitheatre Parkway, Mountain View, CA 94043-1351, or at: 
http://oss.sgi.com/projects/FreeB
Note that, as provided in the License, the Software is distributed on an "AS IS" basis, with ALL EXPRESS AND IMPLIED WARRANTIES AND CONDITIONS DISCLAIMED, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES AND CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
Original Code. The Original Code is: [name of software, version number, and release date], developed by Silicon Graphics, Inc. The Original Code is Copyright (c) [dates of first publication, as appearing in the Notice in the Original Code] Silicon Graphics, Inc. Copyright in any portions created by third parties is as indicated elsewhere herein. All Rights Reserved.
Additional Notice Provisions: [such additional provisions, if any, as appear in the Notice in the Original Code under the heading "Additional Notice Provisions"]

%% The following software may be included in this product:  SunSans and SunSerif. Use of any of this software is governed by the terms of the license below: 

USER AGREEMENT:
SunSans and SunSerif are exclusive property of Sun Microsystems. By loading these fonts you are agreeing to use them ONLY on materials created for Sun Microsystems. If you are not the end user and intend to distribute the fonts, you must include this Read me and .pdf file along with the font files. 

© 1997 Sun Microsystems, Inc., All rights reserved.



