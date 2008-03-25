package com.intellij.psi.impl.cache.impl.repositoryCache;

import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiEllipsisType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.compiled.ClsTypeElementImpl;

/**
 * @author max
 */
public class TypeInfo {
  public String text;
  public byte arrayCount;
  public boolean isEllipsis;

  public static TypeInfo create(PsiType type, PsiTypeElement typeElement) {
    final boolean isEllipsis = type instanceof PsiEllipsisType;
    int arrayCount = type.getArrayDimensions();

    while (typeElement.getFirstChild() instanceof PsiTypeElement) {
      typeElement = (PsiTypeElement)typeElement.getFirstChild();
    }

    String text = typeElement instanceof PsiCompiledElement
                  ? ((ClsTypeElementImpl)typeElement).getCanonicalText()
                  : typeElement.getText();

    TypeInfo result = new TypeInfo();
    result.text = text;
    result.arrayCount = (byte)arrayCount;
    result.isEllipsis = isEllipsis;

    return result;
  }
}
