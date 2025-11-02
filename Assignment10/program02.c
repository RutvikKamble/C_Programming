// WAP whic accept width & height of rectangle from user and calculate its area.
// (Area = Width * height)

#include<stdio.h>

float RectArea(float iNo1, float iNo2)
{
    float fArea = 0.0f;

    fArea = iNo1 * iNo2;

    return fArea;
}

int main()
{
    float fValue1 = 0.0f, fValue2 = 0.0f , fRet = 0.0f;

    printf("Enter the Width : ");
    scanf("%f",&fValue1);

    printf("Enter the Height : ");
    scanf("%f",&fValue2);

    fRet = RectArea(fValue1,fValue2);

    printf("Area of Rectangle is = %f",fRet);

    return 0;
}