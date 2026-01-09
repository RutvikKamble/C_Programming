// Write generic program to accept n values and search first occurance of any specific value

#include<iostream>
using namespace std;

template<class T>
void Display(T *Arr, int iNo)
{
    int iCnt = 0;

    for(iCnt = 0; iCnt < iNo; iCnt++)
    {
        cout<<Arr[iCnt];
        cout<<"\t";
    }
    cout<<"\n";
}

template<class T>
int SearchFirst(T *Arr, int iSize, T Value)
{
    int iCnt = 0;

    for(iCnt = 0; iCnt < iSize; iCnt++)
    {
        if(Arr[iCnt] == Value)
        {
            return iCnt+1;
        }
    }
    return -1;
}

int main()
{
    int *Arr = nullptr;
    
    int iCnt = 0, iSize = 0, iValue = 0;
    int iRet = 0;

    cout<<"Enter the number how many value that you print : ";
    cin>>iSize;

    Arr = new int[iSize];

    for(iCnt = 0; iCnt < iSize; iCnt++)
    {
        cout<<"Enter number : ";
        cin>>Arr[iCnt];
    }

    Display(Arr, iSize);
    
    cout<<"Enter the number to find the first occurance : ";
    cin>>iValue;

    iRet = SearchFirst(Arr, iSize, iValue);

    if(iRet == -1)
    {
        cout<<"the number is not found";
    }
    else
    {
        cout<<"The First Occurance of "<<iValue<<" is : "<<iRet;
    }

    delete []Arr;

    return 0;
}