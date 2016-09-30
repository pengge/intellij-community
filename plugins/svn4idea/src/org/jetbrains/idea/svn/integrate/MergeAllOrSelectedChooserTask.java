/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.svn.integrate;

import com.intellij.openapi.vcs.VcsException;
import com.intellij.util.continuation.TaskDescriptor;
import com.intellij.util.continuation.Where;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MergeAllOrSelectedChooserTask extends BaseMergeTask {

  public MergeAllOrSelectedChooserTask(@NotNull QuickMerge mergeProcess) {
    super(mergeProcess, "merge source selector", Where.AWT);
  }

  @Override
  public void run() {
    //noinspection EnumSwitchStatementWhichMissesCases
    switch (myInteraction.selectMergeVariant()) {
      case all:
        next(getMergeAllTasks());
        break;
      case showLatest:
        LoadRecentBranchRevisions loader = new LoadRecentBranchRevisions(myMergeProcess, -1);
        ShowRecentInDialogTask dialog = new ShowRecentInDialogTask(myMergeProcess, loader);

        next(loader, dialog);
        break;
      case select:
        MergeCalculatorTask calculator = getMergeCalculatorTask();

        if (calculator != null) {
          next(getCalculateFirstCopyPointTask(calculator), calculator);
        }
        break;
    }
  }

  @NotNull
  private TaskDescriptor getCalculateFirstCopyPointTask(@NotNull MergeCalculatorTask mergeCalculator) {
    return myMergeContext.getVcs().getSvnBranchPointsCalculator()
      .getFirstCopyPointTask(myMergeContext.getWcInfo().getRepositoryRoot(), myMergeContext.getWcInfo().getRootUrl(),
                             myMergeContext.getSourceUrl(), mergeCalculator);
  }

  @Nullable
  private MergeCalculatorTask getMergeCalculatorTask() {
    MergeCalculatorTask result = null;

    try {
      result = new MergeCalculatorTask(myMergeProcess);
    }
    catch (VcsException e) {
      end(e);
    }

    return result;
  }
}
