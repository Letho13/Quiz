import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AdminUserService, UserDto} from '../services/admin-user.service';

@Component({
  selector: 'app-admin-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-list.html',
  styleUrls: ['./admin-user-list.scss']
})
export class AdminUserListComponent implements OnInit {
  users: UserDto[] = [];
  searchQuery = '';
  currentPage = 0;
  pageSize = 10;
  hasMorePages: boolean = true;

  constructor(private userService: AdminUserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    const loadObservable = this.searchQuery.trim()
      ? this.userService.searchUsers(this.searchQuery, this.currentPage, this.pageSize)
      : this.userService.getUsers(this.currentPage, this.pageSize);

    loadObservable.subscribe(u => {
      this.users = u;
      // LOGIQUE CLÉ : S'il y a moins d'utilisateurs que la taille de page demandée,
      // cela signifie que nous sommes sur la dernière page.
      this.hasMorePages = u.length === this.pageSize;
    });
  }

  changeRole(user: UserDto, event: Event): void {
    const newRole = (event.target as HTMLSelectElement).value;
    this.userService.updateUserRole(user.id, newRole).subscribe(() => {
      user.role = newRole;
    });
  }

  nextPage(): void {
    // On ne devrait incrémenter que s'il y a potentiellement plus de pages
    if (this.hasMorePages) {
      this.currentPage++;
      this.loadUsers();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadUsers();
    }
  }

  search(): void {
    this.currentPage = 0;
    this.loadUsers();
  }
}
