// Accept Division of student from user and depends on division display exam timing.
// There 4 division in the school as A,B,C,D.
// Exam of division A at 7 am, B at 8.30 am, C at 9.20 am, D at 10.30 am.
// (Application should be case insencetive).

#include<stdio.h>
#include<stdbool.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL;

void CheckAlphabet(char Char)
{
    if((Char == 'A'|| Char == 'a'))
    {
        printf("Your Exam at 7 AM");
    }
    else if (Char == 'B'|| Char == 'b')
    {
        printf("Your Exam at 8.30 AM");
    }
    else if (Char == 'C'|| Char == 'c')
    {
        printf("Your Exam at 9.20 AM");
    }
    else if (Char == 'D'|| Char == 'd')
    {
        printf("Your Exam at 10.30 AM");
    }
    else
    {
        printf("Invalid Input");
    }
}

int main()
{
    char cValue = '\0';

    printf("Enter the Division from A,B,C,D : ");
    scanf("%c",&cValue);

    CheckAlphabet(cValue);

    return 0;
}