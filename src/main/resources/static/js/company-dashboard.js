(function (window, document) {
  'use strict';

  function resolveRoot(context) {
    if (!context) {
      return document.getElementById('companyDashboardRoot');
    }
    if (context instanceof Element) {
      if (context.id === 'companyDashboardRoot') {
        return context;
      }
      return context.querySelector('#companyDashboardRoot');
    }
    if (context && context.target instanceof Element) {
      const target = context.target;
      if (target.id === 'companyDashboardRoot') {
        return target;
      }
      return target.querySelector('#companyDashboardRoot');
    }
    return document.getElementById('companyDashboardRoot');
  }

  function toUpper(value, fallback) {
    if (value === undefined || value === null) {
      value = fallback;
    }
    if (value === undefined || value === null) {
      return '';
    }
    return String(value).trim().toUpperCase();
  }

  function safeHtml(value) {
    if (value === undefined || value === null) {
      return '';
    }
    return String(value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function formatDateTime(value) {
    if (!value) {
      return '-';
    }
    try {
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return '-';
      }
      const yyyy = date.getFullYear();
      const mm = String(date.getMonth() + 1).padStart(2, '0');
      const dd = String(date.getDate()).padStart(2, '0');
      const hh = String(date.getHours()).padStart(2, '0');
      const min = String(date.getMinutes()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
    } catch (_err) {
      return '-';
    }
  }

  function initializeCompanyDashboard(context) {
    const root = resolveRoot(context);
    if (!root || root.dataset.dashboardInitialized === 'true') {
      return;
    }
    root.dataset.dashboardInitialized = 'true';

    const authState = (typeof window !== 'undefined' && window.companyAuth && typeof window.companyAuth === 'object') ? window.companyAuth : {};
    const role = toUpper(root.dataset.role, authState.role || 'COMPANY');
    const status = toUpper(root.dataset.status, authState.membershipStatus || '');
    const isAdmin = role === 'ADMIN';

    const pendingNotice = root.querySelector('#pendingNotice');
    if (pendingNotice) {
      if (!isAdmin && status === 'PENDING') {
        pendingNotice.classList.remove('d-none');
      } else {
        pendingNotice.classList.add('d-none');
      }
    }

    const section = root.querySelector('#adminApprovalSection');
    if (!section) {
      return;
    }

    if (!isAdmin) {
      section.classList.add('d-none');
      return;
    }

    const errorEl = section.querySelector('[data-role="error"]');
    const successEl = section.querySelector('[data-role="success"]');
    const emptyEl = section.querySelector('[data-role="empty"]');
    const tableWrapper = section.querySelector('[data-role="table-wrapper"]');
    const tbody = section.querySelector('[data-role="pending-table-body"]');
    const refreshBtn = section.querySelector('#btnRefreshPending');
    const spinner = section.querySelector('#pendingSpinner');
    const actionHeader = section.querySelector('[data-role="action-header"]');

    const hideSuccess = () => {
      if (successEl) {
        successEl.classList.add('d-none');
        successEl.textContent = '선택한 업체를 승인했습니다.';
      }
    };

    const showSuccess = (message) => {
      if (successEl) {
        successEl.textContent = message;
        successEl.classList.remove('d-none');
      }
    };

    const setState = (state) => {
      hideSuccess();
      if (spinner) spinner.classList.toggle('d-none', state !== 'loading');
      if (refreshBtn) refreshBtn.disabled = state === 'loading';
      if (errorEl) errorEl.classList.toggle('d-none', state !== 'error');
      if (emptyEl) emptyEl.classList.toggle('d-none', state !== 'empty');
      if (tableWrapper) tableWrapper.classList.toggle('d-none', state !== 'table');
      if (actionHeader) {
        actionHeader.textContent = "승인";
        actionHeader.classList.toggle("text-end", state === "table");
      }
    };

    const renderRows = (items) => {
      if (!tbody) {
        return;
      }
      tbody.innerHTML = '';
      items.forEach(item => {
        const tr = document.createElement('tr');
        tr.dataset.userId = String(item.userId);
        tr.dataset.companyId = String(item.companyId);

        const actionCell = isAdmin
          ? '<button class="btn btn-sm btn-success" data-action="approve">승인</button>'
          : '<span class="badge bg-warning text-dark">승인 대기</span>';
        const actionClass = isAdmin ? 'text-end' : 'text-start';

        tr.innerHTML = `
          <td>${safeHtml(item.companyName)}</td>
          <td>${safeHtml(item.businessNumber || '-')}</td>
          <td>
            <div>${safeHtml(item.ceoName || '-')}</div>
            <div class="text-muted small">${safeHtml(item.ceoContact || '')}</div>
          </td>
          <td>
            <div>${safeHtml(item.username || '-')}</div>
            <div class="text-muted small">등록일: ${formatDateTime(item.companyCreatedAt)}</div>
          </td>
          <td class="${actionClass}">
            ${actionCell}
          </td>`;
        tbody.appendChild(tr);
      });
    };

    const loadPending = async (force = false) => {
      const url = force ? `/api/company/pending-requests?t=${Date.now()}` : '/api/company/pending-requests';

      setState('loading');
      if (errorEl) {
        errorEl.textContent = '';
      }

      try {
        const res = await fetch(url, { credentials: 'same-origin', cache: 'no-store' });
        if (!res.ok) {
          throw new Error('목록을 불러오지 못했습니다. (HTTP ' + res.status + ')');
        }
        const data = await res.json();
        if (!Array.isArray(data) || data.length === 0) {
          setState('empty');
          return;
        }
        renderRows(data);
        setState('table');
      } catch (err) {
        if (errorEl) {
          errorEl.textContent = err.message || '목록을 가져오는 중 오류가 발생했습니다.';
        }
        setState('error');
      }
    };

    if (refreshBtn) {
      refreshBtn.addEventListener('click', (event) => {
        event.preventDefault();
        loadPending(true);
      });
    }

    if (isAdmin && tbody) {
      tbody.addEventListener('click', async (event) => {
        const btn = event.target.closest('button[data-action="approve"]');
        if (!btn) {
          return;
        }

        const row = btn.closest('tr');
        if (!row) {
          return;
        }

        const userId = row.dataset.userId;
        const companyId = row.dataset.companyId;
        if (!userId || !companyId) {
          return;
        }

        if (!window.confirm('해당 업체를 승인하시겠습니까?')) {
          return;
        }

        hideSuccess();
        const originalLabel = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>승인중';

        try {
          const res = await fetch(`/api/company/pending/${encodeURIComponent(userId)}/approve`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ companyId: Number(companyId) })
          });
          if (!res.ok) {
            throw new Error('승인 처리에 실패했습니다. (HTTP ' + res.status + ')');
          }

          const companyLabel = row.querySelector('td')?.textContent?.trim() || '선택한 업체';
          row.remove();
          if (!tbody.children.length) {
            setState('empty');
          }
          showSuccess(`${companyLabel} 업체를 승인했습니다.`);
        } catch (err) {
          btn.disabled = false;
          btn.innerHTML = originalLabel;
          window.alert(err.message);
        }
      });
    }

    section.classList.remove('d-none');
    loadPending(false);
  }

  window.initializeCompanyDashboard = initializeCompanyDashboard;
})(window, document);


