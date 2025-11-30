// Accept the character from user and check whether it is alphabet or not(A ot Z, a to z)

#include<stdio.h>
#include<stdbool.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL; 

BOOL CheckAlphabet(char Char)
{
    if((Char >= 'a' && Char <= 'z') || (Char >= 'A' && Char <= 'Z'))
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
        printf("Yes, it is character : ");
    }
    else
    {
        printf("No, it is not character : ");
    }

    return 0;
}

/*
bool CheckAlphabet(char Char)
{
    if((Char >= 'a' && Char <= 'z') || (Char >= 'A' && Char <= 'Z'))
    {
        return true;
    }
    else
    {
        return false;
    }
}


int main()
{
    char cValue = '\0';
    bool bRet = false;

    printf("Enter the character : ");
    scanf("%c",&cValue);

    bRet = CheckAlphabet(cValue);

    if(bRet == true)
    {
        printf("Yes, it is character : ");
    }
    else
    {
        printf("No, it is not character : ");
    }

    return 0;
}

*/

