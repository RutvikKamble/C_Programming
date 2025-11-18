/*
    Algorithm

    START
        Accept N numbers from user
        Show that number of * on screen
    STOP
*/

/////////////////////////////////////////////////////////////////////
//
//  Required Header Files
//
/////////////////////////////////////////////////////////////////////

#include<stdio.h>
#include<stdlib.h>

/////////////////////////////////////////////////////////////////////
//
//  Function Name   : CountEven 
//  Description     : It is used to count even numbers 
//  Input           : int
//  Output          : int
//  Author          : Rutvik Bibhishan Kamble
//  Date            : 16/11/2025
//
/////////////////////////////////////////////////////////////////////

int CountEven(int Arr[], int iSize)
{
    int iCnt = 0, iCount = 0;

    for(iCnt = 0; iCnt < iSize; iCnt++)
    {
        if(iCnt % 2 == 0)
        {
            iCount++;
        }
    }
    return iCount;
}   // End of CountEven

/////////////////////////////////////////////////////////////////////
//
//  Entry point function for the application
//
/////////////////////////////////////////////////////////////////////


int main()
{
    int iLength = 0, iCnt = 0, iRet = 0;
    int *ptr = NULL;

    printf("Enter number of elements : ");
    scanf("%d",&iLength);

    ptr = (int *)malloc(iLength * sizeof(int));
    if(NULL == ptr)
    {
        printf("Unable to allocate the memory");

        return -1;
    }

    printf("Enter %d elements : \n", iLength);
    
    for(iCnt = 0; iCnt < iLength; iCnt++)
    {
        scanf("%d",&ptr[iCnt]);
    }

    iRet = CountEven(ptr,iLength);

    printf("Count of even numbers is : %d ",iRet);

    free(ptr);
    
    return 0;
}   // End of main

/////////////////////////////////////////////////////////////////////
//
//  Testcases successfully handled by the application
//  
//  Input1   : 32, 34, 21, 10, 27      Output  : Count of even numbers is : 3
//  Input2   : 11, 21, 51, 101, 121    Output  : Count of even numbers is : 0
//  
/////////////////////////////////////////////////////////////////////