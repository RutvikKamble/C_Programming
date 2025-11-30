// Accept the character from user and check whether it is Small case or not(A ot Z)

#include<stdio.h>
#include<stdbool.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL;

BOOL CheckAlphabet(char Char)
{
    if((Char >= 'a' && Char <= 'z'))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

int main()
{
    char cValue = '\0';
    BOOL bRet = FALSE;

    printf("Enter the character : ");
    scanf("%c",&cValue);

    bRet = CheckAlphabet(cValue);

    if(bRet == TRUE)
    {
        printf("Yes, The alphabet is Small case : ");
    }
    else
    {
        printf("No, The alphabet is not Small case : ");
    }

    return 0;
}